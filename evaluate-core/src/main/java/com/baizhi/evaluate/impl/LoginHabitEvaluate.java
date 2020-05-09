package com.baizhi.evaluate.impl;

import com.baizhi.entity.EvaluateData;
import com.baizhi.entity.EvaluateItem;
import com.baizhi.entity.EvaluateReport;
import com.baizhi.entity.HistoryData;
import com.baizhi.evaluate.EvaluateChain;
import com.baizhi.evaluate.EvaluateOperator;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginHabitEvaluate implements EvaluateOperator {
    public Integer threshold;

    public LoginHabitEvaluate(Integer threshold) {
        this.threshold = threshold;
    }

    @Override
    public void invoke(EvaluateData evaluateData, HistoryData historyData, EvaluateReport evaluateReport, EvaluateChain chain) {
        Boolean value = evaluateLoginHabit(evaluateData.getCurrentTime(), historyData.getHistoryHabit(), threshold);
        EvaluateItem item = new EvaluateItem();
        item.setComponentName("habit");
        item.setValue(value);
        evaluateReport.getItems().add(item);
        chain.doEvaluate(evaluateData, historyData, evaluateReport);
    }

    /**
     * 登录习惯发生变化(登录时段)
     */
    public Boolean evaluateLoginHabit(long currentTime, Map<String, Map<String, Long>> historyHabit, Integer threshold) {
        // 没有任何的历史的登录习惯数据
        if (historyHabit == null || historyHabit.size() == 0) {
            return false;
        }
        // 习惯数据的比对判断
        // 从当前登录时间中提取周几和用户的登录时段
        String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        // 日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        // 从日历对象中提取出周几信息 西方每一周第一天是周天
        int dayOfWeek = calendar.get(7);
        // 周五
        String strDayOfWeek = weeks[dayOfWeek - 1];

        // 从日历对象中提取出时段信息
        String hourOfDay = calendar.get(11) + "";

        // 如：在当前时间 未出现登录记录
        if (!historyHabit.containsKey(strDayOfWeek)) {
            // 比对用户总的登录次数 > 判定阈值
            // historyHabit
    /*
                周五
                    15  3
                    16  4

                    r=7
                周四
                    12  1
                    20  5
                    r=6

                --------
                    r= 7+6
             */

            Long num = historyHabit
                    .entrySet()
                    .stream()
                    .map(kv -> kv.getValue().entrySet().stream().map(kv2 -> kv2.getValue()).reduce((v1, v2) -> v1 + v2).get())
                    .reduce((v1, v2) -> v1 + v2).get();
            return num > threshold;
        }

        // 判定当前的登录时间是否在习惯时段内
        Map<String, Long> map = historyHabit.get(strDayOfWeek);
        // 如：当前登录时段为17  而历史习惯数据中只有12和20时段结果
        if (!map.containsKey(hourOfDay)) {
            // System.out.println("======");
            return true;
        } else {
            // 计算出用户的习惯数据和偶尔事件数据
            // [1,1,2,6,8,9,10]
            List<Long> sortedList = map
                    .entrySet()
                    .stream()
                    .map(kv -> kv.getValue())
                    .sorted()
                    .collect(Collectors.toList());
            // 假如计算结果为5  判定 时段登录次数大于5 认定为是习惯数据  登录次数小于5 认定为偶然数据
            int thresholdCount = sortedList.size() * 3 / 4;

            // 计算阈值
            // 假如计算结果为5  [习惯数据时段集合]
            List<String> hourList = map.entrySet().stream().filter(kv -> kv.getValue() >= thresholdCount).map(kv -> kv.getKey()).collect(Collectors.toList());

            System.out.println(thresholdCount + "\t" + hourList);
            return !hourList.contains(hourOfDay);
        }
    }
}
