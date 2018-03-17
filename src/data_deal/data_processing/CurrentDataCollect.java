package data_deal.data_processing;

import java.io.BufferedReader;
import java.net.*;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;


/**
 * Created by linguangxiong on 16/10/17.
 *
 * 该类负责一次添加  当前查询的股号（合法并且上市）
 * 只访问一次网页，进行数据抓取 和 处理 成一个个一维数组， 放入Modelist链表属性中
 * 运行完CureentDataCollect，可直接访问到ModelList
 * 所有的方法和变量都为静态
 */
public class CurrentDataCollect {

    private static ArrayList<String[]> ModelList = new ArrayList();          //存储已经整理好的 一维股号数据,
    private static ArrayList<String> CurrentStockNumList = Query.getCurrentData();  //存储传进来的股号，以利于替换
    private static ArrayList<String> HistoryStockNumList = Query.getHistoryData();  //存储传进来的股号，以利于替换

    private static boolean SpreadConnect = true;    //判断网络是否可上
    private static String WarningIsConnect;         //传输过程的提醒



    //刷新操作之后 直接把整理好的数据存储再ModelList中 抓取完数据,可直接访问到已整理好的数据（存储数据模型的链表）
    public static void CurrentRefresh() {
        clearModelList();               //每次开始前 都要对上一次留下的 模型数据 进行清除
        if(!CurrentStockNumList.isEmpty()) {         //传来为非空 则刷新数据
            if (CurrentStockNumList.size() <= 800) {       // 新浪股票网
                String URL = CombineToOneLongURL(CurrentStockNumList);
                CrawlingData(URL, CurrentStockNumList);
            }
        }

    }

    public static void HistoryRefresh() {
        clearModelList();               //每次开始前 都要对上一次留下的 模型数据 进行清除
        if(!HistoryStockNumList.isEmpty()) {         //传来为非空 则刷新数据
            if (HistoryStockNumList.size() <= 800) {       // 新浪股票网
                String URL = HistoryCombineToOneLongURL(HistoryStockNumList);
                CrawlingData(URL, HistoryStockNumList);
            }
            else{
                int Times = (int)Math.ceil(HistoryStockNumList.size() * 1.00 / 800);
                for(int i = 0;i < Times;i++){
                    if(i == Times - 1){
                        ArrayList arrayList = new ArrayList();
                        for(int j = 800 * i;j < HistoryStockNumList.size();j++)
                            arrayList.add(HistoryStockNumList.get(j));

                        String URL = HistoryCombineToOneLongURL(arrayList);
                        CrawlingData(URL,arrayList);
                        System.out.println("----"+ i +"------"+"=-===="+arrayList.size());
                        arrayList.clear();
                    }
                    else{
                        ArrayList arrayList = new ArrayList();
                        for(int j = 800 * i;j < 800 * (i + 1);j++)
                            arrayList.add(HistoryStockNumList.get(j));

                        String URL = HistoryCombineToOneLongURL(arrayList);
                        CrawlingData(URL,arrayList);

                        System.out.println("----"+ i +"------"+"=-===="+arrayList.size());
                        arrayList.clear();
                    }
                }

            }
        }
    }

    //访问该url网页 ，抓取内容  如果传输过程不顺畅则标记清空内容
    public static void CrawlingData(String URLString,ArrayList<String> Numlist) {

        SpreadConnect = true;
        WarningIsConnect = "";

        int readindex = 0;      //读取网页该行的标号
        String IndexContents;

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new URL(URLString).openConnection().getInputStream(), "GBK"));//GBK可以根据需要替换成要读取网页的编码
            while ((IndexContents = in.readLine()) != null) {                       //读取一行内容
                //System.out.println(IndexContents);
                String[] a = IndexContentsFromURL_Split(IndexContents);             //将读取内容剪切 ，返回一个一位数组
                a[32] = Numlist.get(readindex);
                ModelList.add(a);                                                   //将一个一维 数据模型 添加到ModelList
                readindex++;
            }

            //System.out.println("共几行" + readindex);

        } catch (java.net.MalformedURLException ex) {
            System.out.println("Invalid URL");
        } catch (java.io.IOException ex) {
            SpreadConnect = false;
            WarningIsConnect = "数据传输过程断网" ;
            System.out.println("传输过程断网");
        }
    }

    //整合成一大串URL，只需连网访问一次
    public static String CombineToOneLongURL(ArrayList<String> NumList) {

        String URLContents = "http://hq.sinajs.cn/list=";
        for(int i = 0;i < NumList.size();i++)
            URLContents += Query.getSTMarketContents() + NumList.get(i) ;

        System.out.println(URLContents);

        return URLContents;
    }

    //sh：201 202 203 204 5 6 9 //sz:0 1 200 3
    public static String HistoryCombineToOneLongURL(ArrayList<String> NumList) {

        String URLContents = "http://hq.sinajs.cn/list=";
        for(int i = 0;i < NumList.size();i++)
            URLContents += isSH_or_SZ(NumList.get(i)) + NumList.get(i);

        System.out.println(URLContents);

        return URLContents;
    }

    //将抓取的内容进行剪切，并整理放入StockDataList  如果抓取股号为未上市，则字符串数组0标记为空
    public static String[] IndexContentsFromURL_Split(String OneWholeData){

        //先取一行的数据，再取""中的数据
        OneWholeData =
                OneWholeData.substring(OneWholeData.indexOf('"') + 1, OneWholeData.lastIndexOf('"'));
        //再对它进行剪切 ，返回出一个 数据模型的 一维数组
        return OneWholeData.split(",");

    }

    //返回一张 整理好的 一维数据模型 链表
    public static ArrayList<String[]> getModelList() {
        return ModelList;
    }

    //清除一张 整理好的 一维数据模型 链表
    public static void clearModelList(){
        ModelList.clear();
    }

    public static void PrintfModelList(){

        for (int i = 0; i < CurrentDataCollect.getModelList().size(); i++) {
            String[] a = CurrentDataCollect.getModelList().get(i);
            for (int j = 0; j < a.length; j++)
                System.out.print(a[j] + " ");
            System.out.print(a.length + "\n");
        }

    }

    public static boolean isSpreadConnect() {
        return SpreadConnect;
    }

    public static String getWarningIsConnect() {
        return WarningIsConnect;
    }

    //深市股号返回1 //上海股号返回0
    //sh：201 202 203 204 5 6 9 //sz:0 1 200 3
    public static String isSH_or_SZ(String Stnum){

        if(Stnum.charAt(0) == '5'||Stnum.charAt(0) == '6'||Stnum.charAt(0) == '9')
            return ",sh";
        else if(Stnum.charAt(0) == '0'||Stnum.charAt(0) == '1'||Stnum.charAt(0) == '3')
            return ",sz";
        else if(Stnum.charAt(0) == '2'){
            if(Stnum.charAt(2) == '0')
                return ",sz";
            else
                return ",sh" ;
        }
        return "";

    }

}
