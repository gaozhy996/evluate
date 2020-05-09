package com.baizhi;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;
import com.baizhi.evaluate.impl.*;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.UpdaterChain;
import com.baizhi.update.impl.*;
import com.baizhi.util.LogParser;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluateDataTests {
    @Test
    public void test1() throws ParseException {
        // 历史数据
        ArrayList<UpdateOperator> operators = new ArrayList<>();
        operators.add(new HistoryCitiesUpdater());
        operators.add(new HistoryDevicesUpdater());
        operators.add(new HistoryHabitUpdater());
        operators.add(new HistoryPasswordUpdater());
        operators.add(new HistoryInputFeatureUpdater());
        operators.add(new HistoryGeoPointUpdater());
        UpdaterChain chain = new UpdaterChain(operators);
        String loginSuccessLog = "INFO 2019-11-25 14:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        LoginSuccessData loginSuccessData = LogParser.parserLoginSuccessData(loginSuccessLog);
        HistoryData historyData = new HistoryData();
        chain.doUpdate(loginSuccessData, historyData);

        // position重置
        chain.reset();

        String loginSuccessLog2 = "INFO 2019-11-25 15:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"abcdefg\" shanghai \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        LoginSuccessData loginSuccessData2 = LogParser.parserLoginSuccessData(loginSuccessLog2);
        chain.doUpdate(loginSuccessData2, historyData);
        System.out.println(historyData);

        //=====================================================================
        // 评估数据的处理
        List<EvaluateOperator> evaluateOperators = Arrays.asList(
                new LoginCitiesEvaluate(),
                new LoginDistanceEvaluate(500D),
                new LoginCountEvaluate(1),
                new LoginDeviceEvaluate(),
                new LoginHabitEvaluate(10),
                new LoginPasswordEvaluate(0.85),
                new LoginInputFeatureEvaluate()
        );
        EvaluateChain evaluateChain = new EvaluateChain(evaluateOperators);
        String evaluateLog = "INFO 2019-11-25 16:11:00 app1 EVALUATE [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" hangzhou \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.1 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        EvaluateData evaluateData = LogParser.parserEvaluateData(evaluateLog);

        EvaluateReport report = new EvaluateReport();
        evaluateChain.doEvaluate(evaluateData, historyData, report);

        System.out.println(report);
    }
}
