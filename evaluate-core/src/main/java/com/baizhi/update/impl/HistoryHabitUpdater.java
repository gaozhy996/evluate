package com.baizhi.update.impl;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;
import com.baizhi.update.UpdateOperator;
import com.baizhi.update.UpdaterChain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 历史数据中的习惯数据的更新
 */
public class HistoryHabitUpdater implements UpdateOperator {

    @Override
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdaterChain chain) {
        Map<String, Map<String, Long>> historyHabits = historyData.getHistoryHabit();
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
            Map<String, Long> hourOfDayMap = historyHabits.get(strDayOfWeek);
            if (hourOfDayMap.containsKey(hourOfDay)) {
                hourOfDayMap.put(hourOfDay, hourOfDayMap.get(hourOfDay) + 1L);
            } else {
                hourOfDayMap.put(hourOfDay, 1L);
            }
        } else {
            HashMap<String, Long> hourOfDayMap = new HashMap<>();
            hourOfDayMap.put(hourOfDay, 1L);
            historyHabits.put(strDayOfWeek, hourOfDayMap);
        }

        historyData.setHistoryHabit(historyHabits);
        chain.doUpdate(loginSuccessData, historyData);
    }
}
