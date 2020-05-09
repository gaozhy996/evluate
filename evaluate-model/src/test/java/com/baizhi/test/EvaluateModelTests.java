package com.baizhi.test;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EvaluateModelTests {

    public Boolean evalInputFeature(Double[] currentVector, List<Double[]> historyVectors) {
        if (historyVectors == null || historyVectors.size() < 2) {
            return false;
        }

        Double[] sumVector = new Double[currentVector.length];
        for (Double[] historyVector : historyVectors) {
            for (int i = 0; i < historyVector.length; i++) {
                if (sumVector[i] == null) {
                    sumVector[i] = 0.0;
                }
                sumVector[i] += historyVector[i];
            }
        }
        // 求中点
        Double[] centerPoint = new Double[sumVector.length];
        for (int i = 0; i < sumVector.length; i++) {
            centerPoint[i] = sumVector[i] / historyVectors.size();
        }
        System.out.println("center:" + Arrays.stream(centerPoint).map(v -> v + "").reduce((v1, v2) -> v1 + "," + v2).get());

        //  求历史输入特征两两间的距离
        List<Double> distances = new ArrayList<Double>();
        for (int i = 0; i < historyVectors.size(); i++) {
            Double[] vector1 = historyVectors.get(i);
            for (int j = i + 1; j < historyVectors.size(); j++) {
                Double[] vector2 = historyVectors.get(j);
                Double sum = 0.0;
                for (int k = 0; k < vector1.length; k++) {
                    sum += Math.pow(vector1[k] - vector2[k], 2);
                }
                distances.add(Math.sqrt(sum));
            }
        }

        distances = distances.stream().sorted().collect(Collectors.toList());
        int n = historyVectors.size();
        Double threshold = distances.get(n * (n - 1) / 6);
        System.out.println(threshold + "  两点之间距离:" + distances);

        // 计算评估点距中点的距离
        double sum = 0.0;
        for (int i = 0; i < currentVector.length; i++) {
            sum += Math.pow(currentVector[i]-centerPoint[i],2);
        }
        double currentDistance = Math.sqrt(sum);
        System.out.println("评估点距中点的距离："+currentDistance);
        return currentDistance > threshold;
    }

    @Test
    public void test1() {
        //Double[] currentVector = {1.0, 2.0, 3.0};
        Double[] currentVector=new Double[]{1150.0,1400.0,970.0};
        // A B [A,B]  2*1/2 = 1
        // A B C [A,B] [A,C]  [B,C]   3*2/2 = 3
        // A B C D [A,B] [A,C] [A,D]  [B,C] [B,D] ,[C,D] 4*3/2=6
        List<Double[]> historyVectors = Arrays.asList(
                new Double[]{1200.0, 1500.0, 900.0},
                new Double[]{1100.0, 1590.0, 1000.0},
                new Double[]{1300.0, 1320.0, 950.0},
                new Double[]{990.0, 1000.0, 1200.0},
                new Double[]{1400.0, 1780.0, 700.0});
        System.out.println(evalInputFeature(currentVector, historyVectors));
    }

    public Boolean evalPasswordSimilarity(String currentPassword, Set<String> historyPasswords) {
        if (historyPasswords == null || historyPasswords.size() == 0) {
            return false;
        }

        // 构建Bag of words（词袋模型）
        Set<Character> bagOfWords = new HashSet<>();
        historyPasswords.forEach(historyPassword -> {
            char[] charArray = historyPassword.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                bagOfWords.add(charArray[i]);
            }
        });

        char[] currentPasswordCharArray = currentPassword.toCharArray();
        for (int i = 0; i < currentPasswordCharArray.length; i++) {
            bagOfWords.add(currentPasswordCharArray[i]);

        }

        // 构建词袋模型成功
        List<Character> bagOfWordsModel = bagOfWords
                .stream()
                .sorted()
                .collect(Collectors.toList());
        //.forEach(n -> System.out.print(n + "\t"));

        // 创建当前密码的特征向量
        HashMap<Character, Integer> map = new HashMap<>();
        for (char c : currentPasswordCharArray) {
            int count = 0;
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }
        Integer vector[] = new Integer[bagOfWordsModel.size()];

        for (int i = 0; i < bagOfWordsModel.size(); i++) {
            vector[i] = map.get(bagOfWordsModel.get(i)) == null ? 0 : map.get(bagOfWordsModel.get(i));
        }
        // Arrays.stream(vector).forEach(n -> System.out.print(n + "\t"));

        List<Integer[]> historyPasswordVectors = new ArrayList<>();
        historyPasswords.forEach(historyPassword -> {
            char[] tempCharArray = historyPassword.toCharArray();
            HashMap<Character, Integer> tempMap = new HashMap<>();
            for (char c : tempCharArray) {
                int count = 0;
                if (tempMap.containsKey(c)) {
                    tempMap.put(c, tempMap.get(c) + 1);
                } else {
                    tempMap.put(c, 1);
                }
            }
            Integer tempVector[] = new Integer[bagOfWordsModel.size()];

            for (int i = 0; i < bagOfWordsModel.size(); i++) {
                tempVector[i] = tempMap.get(bagOfWordsModel.get(i)) == null ? 0 : tempMap.get(bagOfWordsModel.get(i));
            }
            historyPasswordVectors.add(tempVector);
        });

        System.out.println(historyPasswordVectors.size());

        historyPasswordVectors.forEach(historyPasswordVector -> {
            Integer fz = 0;
            for (int i = 0; i < historyPasswordVector.length; i++) {
                fz += vector[i] * historyPasswordVector[i];
            }

            Double fm = Math.sqrt(Arrays.stream(historyPasswordVector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get()) * Math.sqrt(Arrays.stream(vector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get());
            System.out.println("similarity:" + fz / fm);
        });
        return false;
    }

    @Test
    public void testEvaluatePasswordSimilarity() {
        HashSet<String> history = new HashSet<>();
        history.add("abc456");
        history.add("abc789");
        //evalPasswordSimilarity("aba123", history);
        evalPasswordSimilarity("abc789", history);
    }


    @Test
    public void testSimilarity() {
        //Double[] point1 = {1.0, 2.0, 3.0, 4.0};
        Double[] point1 = {0.0, 1.0};
        Double[] point2 = {0.0, -1.0};
        Double fz = 0.0;
        for (int i = 0; i < point1.length; i++) {
            fz += point1[i] * point2[i];
        }
        Double fm = Math.sqrt(Arrays.stream(point1).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get()) * Math.sqrt(Arrays.stream(point2).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get());

        System.out.println("similarity:" + fz / fm);
    }

    @Test
    public void testEvalLoginHabit() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long currentTime = sdf.parse("2020-02-28 18:10:00").getTime();
        HashMap<String, Map<String, Integer>> historyHabits = new HashMap<>();
        Map<String, Integer> hourMap = new HashMap<>();
        hourMap.put("09", 6);
        hourMap.put("10", 5);
        hourMap.put("14", 2);
        hourMap.put("18", 10);
        hourMap.put("19", 5);
        hourMap.put("20", 3);

        historyHabits.put("星期五", hourMap);

        System.out.println(evalLoginHabit(currentTime, historyHabits, 14));
    }

    public boolean evalLoginHabit(Long currentTime,
                                  Map<String, Map<String, Integer>> historyHabits, Integer threshold) {

        String[] WEEKS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        String dayOfWeek = WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        DecimalFormat decimalFormat = new DecimalFormat("00");
        String hourOfDay = decimalFormat.format(calendar.get(Calendar.HOUR_OF_DAY));

        if (historyHabits == null || historyHabits.size() == 0) {
            return false;
        }
        if (!historyHabits.containsKey(dayOfWeek)) {//不包含
            //计算用户历史登陆的总次数
            Integer historyCount = historyHabits.entrySet()
                    .stream()
                    .map(t -> t.getValue().entrySet().stream().map(v -> v.getValue()).reduce((v1, v2) -> v1 + v2).get())
                    .reduce((v1, v2) -> v1 + v2).get();

            return historyCount > threshold;
        }
        //在当天有登陆记录
        Map<String, Integer> dayHourMap = historyHabits.get(dayOfWeek);
        //没有该时段的登陆记录
        if (!dayHourMap.containsKey(hourOfDay)) {
            return true;
        } else {//有该时段的登陆记录
            Integer loginCount = dayHourMap.get(hourOfDay);

            //升序排列
            List<Integer> sortedList = dayHourMap.entrySet()
                    .stream()
                    .map(t -> t.getValue())
                    .sorted()
                    .collect(Collectors.toList());
            Integer thresholdCount = sortedList.get((sortedList.size() * 2) / 3);
            //获取大于阈值时段的所有hours
            List<String> hourList = dayHourMap.entrySet()
                    .stream()
                    .filter(t -> t.getValue() >= thresholdCount)
                    .map(t -> t.getKey())
                    .collect(Collectors.toList());

            System.out.println("thresholdCount:" + thresholdCount + "　hourList:" + hourList + " currentHour:" + hourOfDay);
            return !hourList.contains(hourOfDay);
        }
    }
}
