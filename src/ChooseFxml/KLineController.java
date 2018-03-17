package ChooseFxml;

import data_deal.data_processing.CurrentDataCollect;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by linguangxiong on 2016/10/31.
 */
public class KLineController implements Initializable {


    public WebView DayWebView   = new WebView();
    public WebView WeekWebView  = new WebView();
    public WebView MonthWebView = new WebView();
    public WebView TimeWebView  = new WebView();
    public ComboBox Refresh;
    private String[] URL = new String[4];
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        DayWebView.getEngine().load(URL[0]);
        WeekWebView.getEngine().load(URL[1]);
        MonthWebView.getEngine().load(URL[2]);
        TimeWebView.getEngine().load(URL[3]);

        Refresh.getItems().addAll("所有","日K线","周K线","月K线","分时线");
        Refresh.setValue("K线刷新");


    }

    public KLineController(String StNum){

        StNum = CurrentDataCollect.isSH_or_SZ(StNum).substring(1) + StNum;
        System.out.println(StNum);

        URL[0] = "http://image.sinajs.cn/newchart/daily/n/" + StNum + ".gif";
        URL[1] = "http://image.sinajs.cn/newchart/weekly/n/" + StNum + ".gif";
        URL[2] = "http://image.sinajs.cn/newchart/monthly/n/" + StNum + ".gif";
        URL[3] = "http://image.sinajs.cn/newchart/min/n/" + StNum + ".gif";

    }

    public void RefreshComoButtonAction() {
        int a = Refresh.getSelectionModel().getSelectedIndex();
        switch (a){
            case 0:
                System.out.println("刷新所有");
                DayWebView.getEngine().load(URL[0]);
                WeekWebView.getEngine().load(URL[1]);
                MonthWebView.getEngine().load(URL[2]);
                TimeWebView.getEngine().load(URL[3]);
                break;
            case 1:
                System.out.println("刷新日k线");
                DayWebView.getEngine().load(URL[0]);
                break;
            case 2:
                System.out.println("刷新周k线");
                WeekWebView.getEngine().load(URL[1]);
                break;
            case 3:
                System.out.println("刷新月k线");
                MonthWebView.getEngine().load(URL[2]);
                break;
            case 4:
                System.out.println("刷新分时k线");
                TimeWebView.getEngine().load(URL[3]);
                break;
        }

    }

}
