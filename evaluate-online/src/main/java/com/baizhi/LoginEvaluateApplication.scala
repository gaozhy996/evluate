package com.baizhi

import java.util.Properties

import com.baizhi.entity.{EvaluateReport, HistoryData}
import com.baizhi.evaluate.impl._
import com.baizhi.evaluate.{EvaluateChain, EvaluateOperator}
import com.baizhi.update.impl._
import com.baizhi.update.{UpdateOperator, UpdaterChain}
import com.baizhi.util.LogParser
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.codehaus.jackson.map.ObjectMapper

object LoginEvaluateApplication {
  def main(args: Array[String]): Unit = {
    //1. 初始化ssc
    val conf = new SparkConf().setAppName("LoginEvaluateApplication").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.sparkContext.setLogLevel("ERROR")
    ssc.checkpoint("file:///Users/gaozhy/data/evaluate_170")

    //2. 初始化kafka的配置对象
    val kafkaParams = Map[String, Object](
      (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "spark:9092"),
      (ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer]),
      (ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer]),
      (ConsumerConfig.GROUP_ID_CONFIG, "g1")
    )

    //3. 准备Array 填写需要订阅的主题Topic
    val arr = Array("login")

    //4. 通过工具类初始化DStream
    val lines = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent, // 位置策略 优化方案
      ConsumerStrategies.Subscribe[String, String](arr, kafkaParams)
    )

    //5. 对数据的处理
    lines
      .map(record => record.value())
      // 排除不符合规则数据
      .filter(input => LogParser.isLegal(input))
      // (uesrId,log)
      .map(input => (LogParser.parserUserId(input), input))
      // 有状态计算
      // State：k= UserId v=HistoryData
      .mapWithState(StateSpec.function((key: String, value: Option[String], state: State[HistoryData]) => {

        var historyData: HistoryData = null
        var evaluateReport: EvaluateReport = null

        if (!state.exists()) {
          // 若：历史状态无，初始化
          historyData = new HistoryData
        } else {
          // 若：历史状态有，更新
          historyData = state.get()
        }

        val log = value.get
        // 判断是否是一个需要评估的日志
        if (LogParser.isEvaluate(log)) {
          // 评估数据的处理
          val evaluateOperators: java.util.List[EvaluateOperator] = java.util.Arrays.asList(
            new LoginCitiesEvaluate,
            new LoginDistanceEvaluate(500D),
            new LoginCountEvaluate(10),
            new LoginDeviceEvaluate,
            new LoginHabitEvaluate(10),
            new LoginPasswordEvaluate(0.85),
            new LoginInputFeatureEvaluate)
          val evaluateChain = new EvaluateChain(evaluateOperators)
          val evaluateData = LogParser.parserEvaluateData(log)
          evaluateReport = new EvaluateReport
          evaluateChain.doEvaluate(evaluateData, historyData, evaluateReport)
        } else if (LogParser.isLoginSuccess(log)) {
          // 判断是否是一个需要登录成功后处理的日志
          // 历史数据
          val operators: java.util.ArrayList[UpdateOperator] = new java.util.ArrayList[UpdateOperator]
          operators.add(new HistoryCitiesUpdater)
          operators.add(new HistoryDevicesUpdater)
          operators.add(new HistoryHabitUpdater)
          operators.add(new HistoryPasswordUpdater)
          operators.add(new HistoryInputFeatureUpdater)
          operators.add(new HistoryGeoPointUpdater)
          val updateChain: UpdaterChain = new UpdaterChain(operators)
          val loginSuccessData = LogParser.parserLoginSuccessData(log)
          updateChain.doUpdate(loginSuccessData, historyData)
        }

        // 更新流式计算的状态
        state.update(historyData)
        println(historyData)
        // 返回评估报告
        evaluateReport
      }))
      .filter(report => report != null)
      .foreachRDD(rdd => rdd.foreachPartition(itar => {
        // 为一个分区创建一个连接
        // 将评估结果写到kafka的另外一个topic中  report
        // bin/kafka-topics.sh --create --topic report --zookeeper spark:2181 --partitions 1 --replication-factor 1
        val prop = new Properties()
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "spark:9092")
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
        prop.put(ProducerConfig.BATCH_SIZE_CONFIG, "100")
        prop.put(ProducerConfig.LINGER_MS_CONFIG, "2000")
        prop.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
        prop.put(ProducerConfig.RETRIES_CONFIG, "3")
        prop.put(ProducerConfig.ACKS_CONFIG, "all")
        val kafkaProducer = new KafkaProducer[String, String](prop)
        itar.foreach(report => {
          // report ---> json
          val jsonReport = new ObjectMapper().writeValueAsString(report)
          val record = new ProducerRecord[String, String]("report", jsonReport)
          kafkaProducer.send(record)
        })
        kafkaProducer.flush()
        //kafkaProducer.close()
      }))

    //6. 启动streaming应用
    ssc.start()

    //7. 优雅的停止应用
    ssc.awaitTermination()
  }
}
