package com.baizhi

import java.text.SimpleDateFormat
import java.util

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema

object UserReportTriggerRateApplication {
  def main(args: Array[String]): Unit = {
    //1. 构建Spark SQL中核心对象SparkSession
    val spark = SparkSession.builder().appName("UserReportTriggerRateApplication").master("local[*]").getOrCreate()
    val df = spark.read.json("/Users/gaozhy/工作/代码仓库/训练营备课代码/BigData/evaluate-offline/src/main/resources")
    df.printSchema()
    import spark.implicits._

    spark.udf.register("dateConvert", (timestamp: Long) => {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val formatDate = sdf.format(timestamp)
      formatDate
    })

    df
      .select($"applicationId", $"currentTime", $"region", $"userId", $"loginSequence", $"items")
      .map(row => {
        val applicationId = row.getAs[String]("applicationId")
        val currentTime = row.getAs[Long]("currentTime")
        val region = row.getAs[String]("region")
        val userId = row.getAs[String]("userId")
        val loginSequence = row.getAs[String]("loginSequence")

        val evalItems = row.getList(5).asInstanceOf[util.List[GenericRowWithSchema]]
        println(evalItems)
        val regionEval = evalItems.get(0).getBoolean(1)
        val distanceEval = evalItems.get(1).getBoolean(1)
        val countEval = evalItems.get(2).getBoolean(1)
        val deviceEval = evalItems.get(3).getBoolean(1)
        val habitEval = evalItems.get(4).getBoolean(1)
        val passwordEval = evalItems.get(5).getBoolean(1)
        val inputFeatureEval = evalItems.get(6).getBoolean(1)

        // 处理评估项
        val list = evalItems
        (applicationId, currentTime, region, userId, loginSequence, regionEval, distanceEval, countEval, deviceEval, habitEval, passwordEval, inputFeatureEval)
      })
      .toDF(
        "appId",
        "current_time",
        "region",
        "user_id",
        "login_sequence",
        "region_eval",
        "distance_eval",
        "count_eval",
        "device_eval",
        "habit_eval",
        "passwordEval",
        "inputFeatureEval"
      )
      .selectExpr("appId",
        "dateConvert(current_time) as current_time",
        "region",
        "user_id",
        "login_sequence",
        "region_eval",
        "distance_eval",
        "count_eval",
        "device_eval",
        "habit_eval",
        "passwordEval",
        "inputFeatureEval")
      .createOrReplaceTempView("t_report")
    val start: String = "2020-03-05 00:00:00"
    val end: String = "2020-03-05 12:08:50"

    spark
      .sql("select * from t_report where current_time >= '" + start + "' and current_time <='" + end + "'")
      .createOrReplaceTempView("t_temp")

    spark
      .sql(
        """
          | select
          |   appId,
          |   sum(case region_eval when true then 1 else 0 end) / count(*) as region_rate,
          |   sum(case distance_eval when true then 1 else 0 end) / count(*) as distance_rate,
          |   sum(case count_eval when true then 1 else 0 end) / count(*) device_rate,
          |   sum(case device_eval when true then 1 else 0 end) / count(*) as habit_rate,
          |   sum(case habit_eval when true then 1 else 0 end) / count(*) as habit_rate,
          |   sum(case passwordEval when true then 1 else 0 end) / count(*) as password_rate,
          |   sum(case inputFeatureEval when true then 1 else 0 end) / count(*) as feature_rate
          | from
          |   t_temp
          | group by
          |   appId
          |""".stripMargin)
      .show()

    // 将评估因子的触发率分析结果写出到外部的存储系统 MySQL中


    spark.stop()
  }
}
