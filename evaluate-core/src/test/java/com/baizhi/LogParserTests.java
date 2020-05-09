package com.baizhi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserTests {
    final static Pattern EVAL_PATTERN = Pattern.compile("^INFO\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(.*)\\sEVALUATE\\s\\[(.+)\\]\\s([a-z0-9]{32})\\s\"(.*)\"\\s([a-z]+)\\s\"([0-9\\.,]+)\"\\s\\[([0-9\\.,]+)\\]\\s\"(.*)\"");
    final static Pattern SUCCESS_PATTERN = Pattern.compile("^INFO\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(.*)\\sSUCCESS\\s\\[(.+)\\]\\s([a-z0-9]{32})\\s\"(.*)\"\\s([a-z]+)\\s\"([0-9\\.,]+)\"\\s\\[([0-9\\.,]+)\\]\\s\"(.*)\"");
    final static Pattern LEGAL_PATTERN = Pattern.compile("^INFO\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(.*)\\s(SUCCESS|EVALUATE)\\s\\[(.+)\\]\\s([a-z0-9]{32})\\s\"(.*)\"\\s([a-z]+)\\s\"([0-9\\.,]+)\"\\s\\[([0-9\\.,]+)\\]\\s\"(.*)\"");

    public static void main(String[] args) {
        String str = "INFO 2020-03-05 11:08:01 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
        Matcher matcher = LEGAL_PATTERN.matcher(str);
        System.out.println(matcher.matches());
    }

//    public static void main(String[] args) throws ParseException {
//        String loginSuccessData = "INFO 2019-11-25 14:11:00 app1 SUCCESS [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
//        Matcher matcher = SUCCESS_PATTERN.matcher();
//        boolean r1 = matcher.matches();
//        System.out.println("是否匹配：" + r1);
//        // 提取数据
//        // long
//        long currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(matcher.group(1)).getTime();
//        String applicationId = matcher.group(2);
//        String userId = matcher.group(3);
//        String loginSequence = matcher.group(4);loginSuccessData
//        // 乱序的明文密码
//        String orderlessPassword = matcher.group(5);
//        String region = matcher.group(6);
//        String[] arrAddress = matcher.group(7).split(",");
//        Double[] geoPoint = {Double.parseDouble(arrAddress[0]), Double.parseDouble(arrAddress[1])};
//
//        String[] arrInputFeature = matcher.group(8).split(",");
//        Double[] inputFeature = new Double[arrInputFeature.length];
//        for (int i = 0; i < arrInputFeature.length; i++) {
//            inputFeature[i] = Double.parseDouble(arrInputFeature[i]);
//        }
//
//        String userAgent = matcher.group(9);
//
//        System.out.println(currentTime + "\n"
//                + applicationId + "\n"
//                + userId + "\n"
//                + loginSequence + "\n"
//                + orderlessPassword + "\n"
//                + region + "\n"
//                + geoPoint + "\n"
//                + userAgent + "\n"
//                + inputFeature);
//    }

//    public static void main(String[] args) throws ParseException {
//        String evaluateLog = "INFO 2019-11-25 14:11:00 app1 EVALUATE [zhangsan01] 6ebaf4ac780f40f486359f3ea6934620 \"123456bCA\" beijing \"116.4,39.5\" [1000,1300.0,1000.0] \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36\"";
//        Matcher matcher = EVAL_PATTERN.matcher(evaluateLog);
//        boolean r1 = matcher.matches();
//        System.out.println("是否匹配：" + r1);
//        // 提取数据
//        // long
//        long currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(matcher.group(1)).getTime();
//        String applicationId = matcher.group(2);
//        String userId = matcher.group(3);
//        String loginSequence = matcher.group(4);
//        // 乱序的明文密码
//        String orderlessPassword = matcher.group(5);
//        String region = matcher.group(6);
//        String[] arrAddress = matcher.group(7).split(",");
//        Double[] geoPoint = {Double.parseDouble(arrAddress[0]),Double.parseDouble(arrAddress[1])};
//
//        String[] arrInputFeature = matcher.group(8).split(",");
//        Double[] inputFeature = new Double[arrInputFeature.length];
//        for (int i = 0; i < arrInputFeature.length; i++) {
//           inputFeature[i] = Double.parseDouble(arrInputFeature[i]);
//        }
//
//        String userAgent = matcher.group(9);
//
//        System.out.println(currentTime + "\n"
//                + applicationId + "\n"
//                + userId + "\n"
//                + loginSequence + "\n"
//                + orderlessPassword + "\n"
//                + region + "\n"
//                + geoPoint + "\n"
//                + userAgent + "\n"
//                + inputFeature);
//    }
}
