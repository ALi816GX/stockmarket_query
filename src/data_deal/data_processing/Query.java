package data_deal.data_processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by linguangxiong on 16/10/17.
 *
 * 该类作用：负责抓去搜索内容,将搜索内容进行分割成单个股票号并存储
 * 实现了抓取方法，切割方法，存储历史股票号，查找，刷新。
 * 剔除非法股号，剔除已经存在的股号，剔除未上市的股号，并保证返回的股号是上市的且合法的！！
 * 所有的方法和变量都为静态
 *
 */

public class Query {

    private static String CatchQueryAllContents = "";  //目的：对接收到的字符串进行处理
    private static String WarningHasExist = "";        //目的：给客户反馈 是否已经存在了
    private static String WarningIsLegal = "";         //目的：给客户反馈 是否为合法的
    private static String WarningIsConnectBefore = ""; //目的：给客户反馈 是否可以上网
    private static String WarningHasInMarket = "";     //目的：给客户反馈 是否已经上市了
    private static int HasNotInMarket = 0;
    private static int HasExist = 0;
    private static String STMarketContents = ",sh";



    private static ArrayList<String> HistoryData = new ArrayList<String>();      //目的：存储历史记录
    private static ArrayList<String> CurrentData = new ArrayList<String>();       //目的: 存储当前记录 预判操作

    //刷新操作
    public static void Refresh(String catchQueryAllContents) {
        setQueryContents(catchQueryAllContents);
        StrSplit();
        RefreshQueryC_H();
        deleNotInMarket();
    }

    //代替数据
    public static void setQueryContents(String catchQueryAllContents) {
        CatchQueryAllContents = catchQueryAllContents;
    }

    //切割
    public static void StrSplit() {

        String[] a = CatchQueryAllContents.split(",");
        CurrentData.clear();       //每次进行前先 清空之前的字符串
        for (int i = 0; i < a.length; i++)
            CurrentData.add(a[i]); //往 当前股号链表 添加股号，以便判断

    }

    //对 当前股号链表 和 历史股号链表 的刷新 且对提醒字符的更改
    public static void RefreshQueryC_H() {

        WarningHasExist = "";
        WarningIsLegal = "";
        HasExist = 0;

        for (int i = 0; i < CurrentData.size(); i++) {

            String judgeStr = CurrentData.get(i);
            int judgeExist = IsExistInHistoryStock(judgeStr);

            switch (judgeExist) {

                case 1://存在
                    WarningHasExist += judgeStr + " ";
                    CurrentData.remove(i);
                    HasExist++;
                    i--;
                    break;

                case 0://不存在
                    int judgeLegal = IsLegal(judgeStr);

                    switch (judgeLegal) {

                        case 1://合法
                            HistoryData.add(judgeStr);
                            break;

                        case 0://非法
                            WarningIsLegal += judgeStr + " ";
                            CurrentData.remove(i);
                            i--;
                            break;
                    }
                    break;
            }
        }

        if (!WarningHasExist.equals(""))
            WarningHasExist += "股号已经存在";
        if (!WarningIsLegal.equals(""))
            WarningIsLegal += "非法股号(应为纯6为数字)";

    }


    //判断是否与历史记录存在 ，是返回1 ，否则返回 0
    public static int IsExistInHistoryStock(String a) {
        if (HistoryData.size() == 0)
            return 0;
        for (int i = 0; i < HistoryData.size(); i++)
            if (a.equals(HistoryData.get(i)))
                return 1;

        return 0;
    }

    //判断字符串是否合法 是返回1 ，否则返回 0
    public static int IsLegal(String a) {
        if (a.length() != 6)
            return 0;
        for (int i = 0; i < a.length(); i++)
            if (!Character.isDigit(a.charAt(i))) {
                return 0;
            }
        return 1;
    }

    //删除未上市的股号 并更新当前 当前股号链表 和 历史股号链表 的刷新 且对上市提醒字符的更改
    public static void deleNotInMarket(){

        WarningHasInMarket = "";
        HasNotInMarket = 0;

        String URLContents = "http://hq.sinajs.cn/list=";
        for(int i = 0;i < CurrentData.size();i++)
            URLContents += STMarketContents + CurrentData.get(i);      //整合成一大串URL，只需访问一次

        System.out.println(URLContents);

        String aa;
        int readindex = 0;      //该下标
        if(!URLContents.equals("http://hq.sinajs.cn/list=")) {                  //防止空网站造成io异常

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new URL(URLContents).openConnection().getInputStream(), "GBK"));//GBK可以根据需要替换成要读取网页的编码
                while ((aa = in.readLine()) != null) {
                    if (aa.length() == 23) {  //23代表上市的字符串长度
                        WarningHasInMarket += CurrentData.get(readindex) + " ";
                        DeleOneHistoryData(CurrentData.get(readindex));          //删除历史记录中的 上市股号；
                        CurrentData.remove(readindex);                           //删除当前记录中的 上市股号；
                        readindex--;                                             //删除了该号，则要--，防止越界
                        HasNotInMarket++;
                        System.out.println(aa);
                    }
                    readindex++;
                }

            } catch (java.net.MalformedURLException ex) {
                System.out.println("Invalid URL");
            } catch (java.io.IOException ex) {
                System.out.println("传输过程断网");
            } finally {
                if (!WarningHasInMarket.equals(""))
                    WarningHasInMarket += "股号未上市";
            }
        }
    }

    //判断能否正常上网
    public static boolean IsConnectBefore() {   //true连上 false 没网络
        WarningIsConnectBefore = "";
        boolean IsConnectBefore = true;
        URL url;
        try {
            url = new URL("http://baidu.com");
            try {
                InputStream in = url.openStream();
                in.close();
                System.out.println("网络连接正常！");
                IsConnectBefore = true;
            } catch (IOException e) {
                WarningIsConnectBefore = "网络连接失败";
                System.out.println("网络连接失败！");
                IsConnectBefore = false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            return IsConnectBefore;
        }
    }

    //股市刷新时间为每周一到周五上午时段9:30-11:30，下午时段13:00-15:00。周六、周日上海证券交易所、深圳证券交易所公告的休市日不交易。
    //正常交易返回true   否则返回false
    public static boolean IsTimeRefresh() {

        Calendar now = Calendar.getInstance();
        //一周第一天是否为星期天
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        //获取周几
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
        //若一周第一天为星期天，则-1
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }


        //判断为周末 //周末股市不开放
        if (weekDay == 6 || weekDay == 7) {
            return false;
        }
        else {

            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            int second = now.get(Calendar.SECOND);

            long CurrentDayMillis = hour*3600*1000+minute*60*1000+second*1000;  //当天的时间转化为毫秒

            //9:30  11:30 13:00 15:00 转化为 mills
            long Time9_30  = 9*3600*1000 + 30*60*1000;
            long Time11_30 = 11*3600*1000 + 30*60*1000;
            long Time13_00 = 13*3600*1000;
            long Time15_00 = 15*3600*1000;

            if((CurrentDayMillis >= Time9_30 && CurrentDayMillis <= Time11_30)||
                    (CurrentDayMillis >= Time13_00 && CurrentDayMillis <= Time15_00))
                return true;
            else
                return false;

        }

    }

    //删除存储历史记录中的条目  倒序查
    public static void DeleOneHistoryData(String StockNum){
        for(int i = HistoryData.size() - 1;i >= 0;i--)
            if(HistoryData.get(i).equals(StockNum)){
                HistoryData.remove(i);
                break;
            }
    }


    //返回是否能上网字符串
    public static String getWarningIsConnectBefore() {
        return WarningIsConnectBefore;
    }
    //返回除了
    public static String getWarning() {
        return WarningHasExist + " " + WarningHasInMarket + " " + WarningIsLegal + " 成功添加" + CurrentData.size() + "条" ;
    }

    public static ArrayList<String> getHistoryData() {
        return HistoryData;
    }

    public static ArrayList<String> getCurrentData() {
        return CurrentData;
    }

    //空返回ture 否则返回false
    public static boolean IsHistoryDataEmpty(){
        if((Query.getHistoryData().size() == 0))
            return true;
        return false;
    }

    public static boolean IsCurrentDataEmpty(){
        if((Query.getCurrentData().size() == 0))
            return true;
        return false;
    }

    public static int getHasNotInMarket() {
        return HasNotInMarket;
    }

    public static int getHasExist() {
        return HasExist;
    }

    public static void setSTMarketContents(String STMarketContents) {
        Query.STMarketContents = STMarketContents;
    }

    public static String getSTMarketContents() {
        return STMarketContents;
    }


}
