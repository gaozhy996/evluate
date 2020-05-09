package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateChain;
import com.baizhi.update.UpdateOperator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HistoryLoginHabitUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain) {
        Map<String, Map<String, Long>> historyHabits = historyData.getHistoryHabits();
        if (historyHabits == null) {
            historyHabits = new HashMap<>();
        }
        // 习惯数据的比对判断
        // 从当前登录时间中提取周几和用户的登录时段
        String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        // 日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(loginSuccessData.getCurrentTime());

        // 从日历对象中提取出周几信息 西方每一周第一天是周天
        int dayOfWeek = calendar.get(7);
        // 周五
        String strDayOfWeek = weeks[dayOfWeek - 1];

        // 从日历对象中提取出时段信息
        String hourOfDay = calendar.get(11) + "";

        if (historyHabits.containsKey(strDayOfWeek)) {
            Map<String, Long> hourOfMap = historyHabits.get(strDayOfWeek);
            if (hourOfMap.containsKey(hourOfDay)) {
                hourOfMap.put(hourOfDay, hourOfMap.get(hourOfDay) + 1L);
            } else {
                hourOfMap.put(hourOfDay, 1l);
            }
        } else {
            HashMap<String, Long> hourOfMap = new HashMap<>();
            hourOfMap.put(hourOfDay, 1L);
            historyHabits.put(strDayOfWeek, hourOfMap);
        }
        historyData.setHistoryHabits(historyHabits);
        updateChain.doChain(loginSuccessData,historyData);
    }
}
