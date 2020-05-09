package com.baizhi;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评估模型的测试类
 */
public class EvaluateModelTests {
    @Test
    public void testEvaluateInputFeatures() {
        // List<Double[]> historyVectors = Arrays.asList(new Double[]{100.0, 200.0, 300.0}, new Double[]{200.0, 300.0, 400.0});
        List<Double[]> historyVectors = Arrays.asList(
                new Double[]{100.0, 200.0},
                new Double[]{200.0, 300.0},
                new Double[]{400.0, 500.0},
                new Double[]{300.0, 400.0});
        evaluateInputFeatures(new Double[]{200.0, 299.0}, historyVectors);
    }

    /**
     * 用户输入特征的相似度的判定
     *
     * @currentVector 业务系统提供当前登录的输入特征向量  如：[100,200,300]
     * @historyVectors 用户历史的登录行为输入特征向量(只保留用户最新的10个特征向量)
     */
    public Boolean evaluateInputFeatures(Double[] currentVector, List<Double[]> historyVectors) {
        if (historyVectors == null || historyVectors.size() < 2) {
            return false;
        }
        // 1. 首先计算出历史的输入特征向量的中心点
        // [100,200,300]  [200,300,400]  ==> [150,250,350]
        // 相同纬度的值相加 / 长度 = 中心点纬度值
        Double[] sumVector = new Double[currentVector.length];
        for (Double[] historyVector : historyVectors) {
            for (int i = 0; i < historyVector.length; i++) {
                if (sumVector[i] == null) {
                    sumVector[i] = 0.0;
                }
                sumVector[i] += historyVector[i];
            }
        }
        Double[] centerPoint = new Double[currentVector.length];
        for (int i = 0; i < centerPoint.length; i++) {
            centerPoint[i] = sumVector[i] / historyVectors.size();
        }
        Arrays.stream(centerPoint).forEach(n -> System.out.print(n + ","));

        // 2. 求出历史登录特征两两之间的距离
        // p1: [A,B]  p2:[X,Y]    p1->p2
        // p1: [A,B]  p2:[X,Y] P3:[M,N]  p1->p2  p1->p3 p2->p3
        // p1: [A,B]  p2:[X,Y] P3:[M,N] p4:[J,K]  p1->p2  p1->p3 p1->p4  p2->p3 p2->p4  p3->p4
        // 等差数列 n*(n-1)/2 * 1/3
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < historyVectors.size(); i++) {
            Double[] p1 = historyVectors.get(i);
            for (int j = i + 1; j < historyVectors.size(); j++) {
                Double[] p2 = historyVectors.get(j);
                // 求两点之间的距离
                Double sum = 0.0;
                for (int k = 0; k < p1.length; k++) {
                    sum += Math.pow(p1[k] - p2[k], 2);
                }
                Double distance = Math.sqrt(sum);
                distances.add(distance);
            }
        }
        System.out.println(distances);

        // 3. 对历史行为特征距离集合的样本数据 升序排列，取1/3位置的结果作为 判定阈值
        distances = distances.stream().sorted().collect(Collectors.toList());

        // 计算安全阈值
        double threshold = distances.get(historyVectors.size() * (historyVectors.size() - 1) / 6);

        // 4. 当前输入点和中心点距离
        Double sum = 0.0;

        for (int i = 0; i < currentVector.length; i++) {
            sum += Math.pow(currentVector[i] - centerPoint[i],2);
        }

        System.out.println("当前输入点和中心点距离："+Math.sqrt(sum) +"  安全阈值："+threshold);

        return Math.sqrt(sum) > threshold;
    }


    /**
     * 密码相似特征评估
     *
     * @currentPassword 当前登录密码
     * @historyPasswords 用户的历史密码的集合
     * @threshold 安全阈值
     */
    public Boolean evaluateLoginPasswordSimilarity(String currentPassword, Set<String> historyPasswords, Double threshold) {
        if (historyPasswords == null || historyPasswords.size() == 0) {
            return false;
        }

        // 建立词袋子模型（历史密码 + 当前密码）  bag of words model
        Set<Character> bagOfWords = new HashSet<>();
        char[] currentPasswordCharArray = currentPassword.toCharArray();
        for (int i = 0; i < currentPasswordCharArray.length; i++) {
            bagOfWords.add(currentPasswordCharArray[i]);
        }

        for (String historyPassword : historyPasswords) {
            char[] historyPasswordCharArray = historyPassword.toCharArray();
            for (int i = 0; i < historyPasswordCharArray.length; i++) {
                bagOfWords.add(historyPasswordCharArray[i]);
            }
        }
        // 建立最终的词袋子模型
        List<Character> bagOfWordsModel = bagOfWords.stream().sorted().collect(Collectors.toList());
        bagOfWordsModel.stream().forEach(n -> System.out.print(n + "\t"));
        System.out.println();
        // 建立当前登录密码向量模型
        // map记录 当前密码中字符的出现次数
        // aba123
        // a 2
        // b 1
        // 1 1
        // 2 1
        // 3 1
        HashMap<Character, Integer> charMap = new HashMap<>();
        for (int i = 0; i < currentPasswordCharArray.length; i++) {
            char c = currentPasswordCharArray[i];
            if (charMap.containsKey(c)) {
                charMap.put(c, charMap.get(c) + 1);
            } else {
                charMap.put(c, 1);
            }
        }
        // 向量的纬度等价于词袋子长度
        Integer[] currentPasswordVector = new Integer[bagOfWordsModel.size()];
        for (int i = 0; i < bagOfWordsModel.size(); i++) {
            if (charMap.containsKey(bagOfWordsModel.get(i))) {
                currentPasswordVector[i] = charMap.get(bagOfWordsModel.get(i));
            } else {
                currentPasswordVector[i] = 0;
            }
        }
        Arrays.stream(currentPasswordVector).forEach(n -> System.out.print(n + "\t"));

        // 建立历史登录密码的向量
        List<Integer[]> historyPasswordVectors = new ArrayList<>();
        for (String historyPassword : historyPasswords) {
            HashMap<Character, Integer> tempCharMap = new HashMap<>();
            char[] historyPasswordCharArray = historyPassword.toCharArray();
            for (int i = 0; i < historyPasswordCharArray.length; i++) {
                char c = historyPasswordCharArray[i];
                if (tempCharMap.containsKey(c)) {
                    tempCharMap.put(c, tempCharMap.get(c) + 1);
                } else {
                    tempCharMap.put(c, 1);
                }
            }
            // 向量的纬度等价于词袋子长度
            Integer[] historyPasswordVector = new Integer[bagOfWordsModel.size()];
            for (int i = 0; i < bagOfWordsModel.size(); i++) {
                if (tempCharMap.containsKey(bagOfWordsModel.get(i))) {
                    historyPasswordVector[i] = tempCharMap.get(bagOfWordsModel.get(i));
                } else {
                    historyPasswordVector[i] = 0;
                }
            }
            historyPasswordVectors.add(historyPasswordVector);
        }
        // 求向量夹角
        for (Integer[] historyPasswordVector : historyPasswordVectors) {
            Double fz = 0.0;
            for (int i = 0; i < historyPasswordVector.length; i++) {
                fz += historyPasswordVector[i] * currentPasswordVector[i];
            }

            Double fm = Math.sqrt(Arrays.stream(historyPasswordVector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get())
                    * Math.sqrt(Arrays.stream(currentPasswordVector).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get());
            System.out.println("相似度：" + fz / fm);
            if ((fz / fm) > threshold) {
                return false;
            }
        }
        return true;
    }


    @Test
    public void testEvaluateLoginPasswordSimilarity() {
        HashSet<String> historyPasswords = new HashSet<>();
        historyPasswords.add("abc12345");
        historyPasswords.add("abc123");
        // 词袋子：a b c 1 2 3 4 5
        Boolean result = evaluateLoginPasswordSimilarity("xyzjqk784", historyPasswords, 0.8);
        // evaluateLoginPasswordSimilarity("xyzjqk784", historyPasswords, 0.5);
        System.out.println("评估结果：" + result);
    }

    /**
     * x1*x2 + *y1*y2
     * cos(∠AOB) = -------------------------------------------
     * sqrt( x1² + y1²)*sqrt(x2² + y2²)
     */
    @Test
    public void testSimilarity() {
        Double point1[] = {100.0, 0.0};
        Double point2[] = {-1000.0, 0.0};
        Double fz = 0.0;
        for (int i = 0; i < point1.length; i++) {
            fz += point1[i] * point2[i];
        }
        Double fm = Math.sqrt(Arrays.stream(point1).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get())
                * Math.sqrt(Arrays.stream(point2).map(n -> n * n).reduce((v1, v2) -> v1 + v2).get());
        System.out.println("相似度：" + fz / fm);
    }

    @Test
    public void test2() {
        String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        // 日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // 从日志对象中提取出周几信息 西方每一周第一天是周天
        int dayOfWeek = calendar.get(7);
        // 周五
        String strDayOfWeek = weeks[dayOfWeek - 1];

        int hourOfDay = calendar.get(11);

        System.out.println(strDayOfWeek + "\t" + hourOfDay);
    }

    @Test
    public void test3() {
        HashMap<String, Map<String, Long>> historyHabit = new HashMap<>();
        HashMap<String, Long> hourOfDay = new HashMap<>();
        hourOfDay.put("12", 10L);
        hourOfDay.put("13", 1L);
        hourOfDay.put("17", 9L);
        hourOfDay.put("18", 2L);
        hourOfDay.put("20", 4L); // 1 3 4 9 10   5*3/4 = 3
        historyHabit.put("周五", hourOfDay);

        Boolean result = evaluateLoginHabit(System.currentTimeMillis(), historyHabit, 10);
        System.out.println("评估结果：" + result);
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
            System.out.println("======");
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


    /**
     * 登录设备发生变化
     */
    public Boolean evaluateLoginDevice(String currentDevice, Set<String> historyDevices) {
        // 新用户 无历史的登录设备信息
        if (historyDevices == null || historyDevices.size() == 0) {
            return false;
        }
        //
        return !historyDevices.contains(currentDevice);
    }

    /**
     * 频繁登录（登录次数限定）
     * map(day,count)
     */
    public Boolean evaluateLoginCount(Integer currentDayCounts, Integer threshold) {
        if (currentDayCounts == null || currentDayCounts == 0) {
            return false;
        }
        // 实际登录次数大于要求阈值  风险
        return currentDayCounts + 1 > threshold;
    }

    /**
     * 基于用户登录城市信息
     *
     * @return true 有风险 false 无风险
     * @currentCity 当前城市
     * @historyCities 历史登录城市
     */
    public Boolean evaluateLoginCity(String currentCity, Set<String> historyCities) {
        // 首次登录
        if (historyCities == null || historyCities.size() == 0) {
            return false;
        }
        // true 常用城市登录
        // false 头一次出现
        return !historyCities.contains(currentCity);
    }

    /**
     * // bj [116.404305,39.914658]
     * // sh [121.4975,31.242227]
     * <p>
     * 基于用户位移距离的风险判定
     *
     * @currentLoginTime
     * @aPoint 当前位置坐标
     * @bPoint 上一次登录位置坐标
     */
    public Boolean evaluateLoginAddress(long currentLoginTime, Double[] aPoint, long lastLoginTime, Double[] bPoint, Double speedThreshold) {
        // AB两点弧长距离计算公式
        // R*arccos(cos(wA)*cos(wB)*cos(jB-jA) + sin(wB)*sin(wA))
        // 6371
        // wA wB jB jA

        // nπR/180  角度 ---> 弧度
        Double wA = aPoint[1] * Math.PI / 180;
        Double wB = bPoint[1] * Math.PI / 180;
        Double jA = aPoint[0] * Math.PI / 180;
        Double jB = bPoint[0] * Math.PI / 180;

        Double distance = 6371 * Math.acos(Math.cos(wA) * Math.cos(wB) * Math.cos(jB - jA) + Math.sin(wB) * Math.sin(wA));

        // 1200km  500KM/h
        double totalTime = distance / speedThreshold;

        System.out.println("A点到B点的距离:" + distance + "\t 理论位移时长:" + totalTime);
        // 2.4小时

        // 理论时间 > 实际时间
        if ((totalTime * 3600 * 1000) > (currentLoginTime - lastLoginTime)) {
            return true;
        } else {
            return false;
        }
    }

    @Test
    public void test1() {
        Boolean result = evaluateLoginAddress(
                1582873200000L,
                new Double[]{116.404305, 39.914658},
                1582855200000L,
                new Double[]{87.534751, 43.846993},
                500D);
        System.out.println("评估结果：" + result);
    }
}
