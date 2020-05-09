package com.baizhi.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserTest {
    final static Pattern EVAL_PATTERN = Pattern.compile("^INFO\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(.*)\\sEVALUATE\\s\\[(.+)\\]\\s([a-z0-9]{32})\\s\"(.*)\"\\s([a-z]+)\\s\"([0-9\\.,]+)\"\\s\\[([0-9\\.,]+)\\]\\s\"(.*)\"");
    final static Pattern SUCCESS_PATTERN = Pattern.compile("^INFO\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(.*)\\sSUCCESS\\s\\[(.+)\\]\\s([a-z0-9]{32})\\s\"(.*)\"\\s([a-z]+)\\s\"([0-9\\.,]+)\"\\s\\[([0-9\\.,]+)\\]\\s\"(.*)\"");

    public static void main(String[] args) throws ParseException {
        // 级别|日期|用户名|登陆序列号|密码(乱序)|日志类型|应用信息|城市|经纬度|输入特征|设备信息
        String evalData = "INFO 2019-11-25 14:11:00 app1 EVALUATE [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        String loginSuccessData = "INFO 2019-11-25 14:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";

        boolean r1 = EVAL_PATTERN.matcher(evalData).matches();
        boolean r2 = EVAL_PATTERN.matcher(loginSuccessData).matches();
        System.out.println(r1);
        System.out.println(r2);
        paserEvalauteData(evalData);
    }

    public static void paserEvalauteData(String input) throws ParseException {
        Matcher matcher = EVAL_PATTERN.matcher(input);
        matcher.matches();
        long currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(matcher.group(1)).getTime();
        String applicationID = matcher.group(2);
        String userID = matcher.group(3);
        String loginSequence = matcher.group(4);
        String orderlessPassword = matcher.group(5);
        String region = matcher.group(6);
        Double[] geoPoint = new Double[2];
        String[] splitGEO = matcher.group(7).split(",");
        geoPoint[0] = Double.parseDouble(splitGEO[0]);
        geoPoint[1] = Double.parseDouble(splitGEO[1]);

        String[] inputFeatureTokens = matcher.group(8).split(",");
        Double[] inputFeature = new Double[inputFeatureTokens.length];
        for (int i = 0; i < inputFeature.length; i++) {
            inputFeature[i] = Double.parseDouble(inputFeatureTokens[i]);
        }
        String userAgent = matcher.group(9);

        System.out.println(currentTime + " " + applicationID + " " + userID + " " + loginSequence + " " + orderlessPassword + " "+region + " "+geoPoint + " " + inputFeature + "" + userAgent);
    }
}
