package sample;

import ChooseFxml.KLineController;
import ChooseFxml.KLineStageManager;
import data_deal.data_processing.Query;
import data_deal.data_processing.CurrentDataCollect;
import data_deal.data_stock.StockDataModel;
import data_deal.data_stock.StockDataModelManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    //功能类
    private ObservableList<StockDataModel> observableList;
    private StockDataModelManager SDMmanager;
    private KLineStageManager KSmanager;
    private Timeline animatation;
    private String shanghaiSTNumList[];
    private String shenzhenSTNumList[];


    //控件
    public TableView tableView;
    public TextField ScannerContents;
    public TextField QueryContents;
    public TextField SecondsContents;
    public TextField StockNumContents;
    public Label  WarningSerchNum;
    public Label  WarningScannerNum;
    public Label  WarningRefreshTime;
    public Label  WarningIsExistStNum;
    public ComboBox<String> AllStockNumList;
    public ComboBox<String> STMarket;
//    public WebView web;


    //初始化数据
    @Override
    public void initialize(URL location, ResourceBundle resources)  {

        Add_TableColumn_To_tableView();
        this.observableList  = FXCollections.observableArrayList();
        SDMmanager = StockDataModelManager.getInstance();
        KSmanager  = KLineStageManager.getInstance();
        shanghaiSTNumList = ReadSTNumList("/Users/linguangxiong/Documents/java workspace2/stockmarket_query/src/正则扫描记事本选择器/shanghai.txt",shanghaiSTNumList);
        shenzhenSTNumList = ReadSTNumList("/Users/linguangxiong/Documents/java workspace2/stockmarket_query/src/正则扫描记事本选择器/shenzhen.txt",shenzhenSTNumList);

        AllStockNumList.getItems().addAll("国内上市股票","上海股票","深圳股票");
        AllStockNumList.setValue("添加股票");

        STMarket.getItems().addAll("沪市","深市");
        STMarket.setValue("沪市");

        animatation = new Timeline(new KeyFrame(Duration.seconds(10),event -> {
            ManuRefreshesButtonAction();
        }));
        animatation.setCycleCount(Timeline.INDEFINITE);


    }

    //将列标添加如tableview
    public void Add_TableColumn_To_tableView() {

        //获取tableView列的ObservableList 保存引用 方便之后插入列
        ObservableList<TableColumn> tableColumnsSet = tableView.getColumns();

        //创建TableColumn列
        TableColumn<StockDataModel,String>[] tableColumn = new TableColumn[33];

        tableColumn[0]  = new TableColumn("股票名字");
        tableColumn[1]  = new TableColumn("今日开盘价");
        tableColumn[2]  = new TableColumn("昨日收盘价");
        tableColumn[3]  = new TableColumn("当前价格");
        tableColumn[4]  = new TableColumn("今日最高价");
        tableColumn[5]  = new TableColumn("今日最低价");
        tableColumn[6]  = new TableColumn("竞买价");
        tableColumn[7]  = new TableColumn("竞卖价");
        tableColumn[8]  = new TableColumn("成交的股票数");
        tableColumn[8].setMinWidth(100);
        tableColumn[9]  = new TableColumn("成交金额(万)");
        tableColumn[9].setMinWidth(120);
        tableColumn[10] = new TableColumn("买一申请股数");
        tableColumn[11] = new TableColumn("买一报价(元)");
        tableColumn[12] = new TableColumn("买二申请股数");
        tableColumn[13] = new TableColumn("买二报价(元)");
        tableColumn[14] = new TableColumn("买三申请股数");
        tableColumn[15] = new TableColumn("买三报价(元)");
        tableColumn[16] = new TableColumn("买四申请股数");
        tableColumn[17] = new TableColumn("买四报价(元)");
        tableColumn[18] = new TableColumn("买五申请股数");
        tableColumn[19] = new TableColumn("买五报价(元)");
        tableColumn[20] = new TableColumn("卖一申请股数");
        tableColumn[21] = new TableColumn("卖一报价(元)");
        tableColumn[22] = new TableColumn("卖二申请股数");
        tableColumn[23] = new TableColumn("卖二申请股数");
        tableColumn[24] = new TableColumn("卖三申请股数");
        tableColumn[25] = new TableColumn("卖三申请股数");
        tableColumn[26] = new TableColumn("卖四申请股数");
        tableColumn[27] = new TableColumn("卖四申请股数");
        tableColumn[28] = new TableColumn("卖五申请股数");
        tableColumn[29] = new TableColumn("卖五申请股数");
        tableColumn[30] = new TableColumn("日期");
        tableColumn[31] = new TableColumn("当前时间");
        tableColumn[32] = new TableColumn("股号") ;

        for (int i = 0; i < 33; i++) {

            int finalI = i;

            //使用回调的方式绑定列于模型对应的数组下标
            tableColumn[i].setCellValueFactory(e -> new SimpleObjectProperty(e.getValue().getstockDataCell(finalI)));

            //将实例化的列加入tableView
            tableColumnsSet.add(tableColumn[i]);

        }

    }

    //添加数据入tableview列表
    public void AddNewData_To_tableView(){
        // 刷新完数据 如果搜索内容返回来的为空 则不进行抓取
        if (!Query.IsCurrentDataEmpty()) {
            CurrentDataCollect.CurrentRefresh();
            ArrayList<String[]> a = CurrentDataCollect.getModelList();
            for(int i = 0;i < a.size();i++){
                StockDataModel b = new StockDataModel(a.get(i));
                SDMmanager.add(b);
                observableList.add(b);
            }
            tableView.setItems(observableList);
            //CurrentDataCollect.PrintfModelList();
        }
    }

    //刷新tableview数据列表
    public void RefreshTableView(){

        // 刷新完数据 如果搜索内容返回来的为空 则不进行抓取
        if (!Query.IsHistoryDataEmpty()) {
            CurrentDataCollect.HistoryRefresh();
            ArrayList<String[]> a = CurrentDataCollect.getModelList();
            System.out.println("----------"+a.size());
            for (int i = 0; i < a.size(); i++) {
                StockDataModel b = new StockDataModel(a.get(i));
                SDMmanager.getModelaList().set(i, b);
                //SDMmanager.getModelaList().get(0).setStockData(a.get(i));
                observableList.set(i,b);
            }
            tableView.setItems(observableList);
            //CurrentDataCollect.PrintfModelList();
        }


    }

    //获取整理好的股票字符串
    public String getScannerStockListNum(){
        //该属性为返回的股票代码串
        String StockNumList = "";

        String StrContents = ScannerContents.getText();
        boolean IsLegal = true;
        int StrLength = StrContents.length();

        if(StrLength == 13) {
            for (int i = 0;i < StrLength;i++){
                if(i == 6){
                    if(!(StrContents.charAt(i) == '-')){
                        WarningScannerNum.setText("格式:股号-股号,最多扫描850条");
                        IsLegal = false;
                        break;
                    }

                }
                else{
                    if(!Character.isDigit(StrContents.charAt(i))) {
                        WarningScannerNum.setText("格式:股号-股号,最多扫描850条");
                        IsLegal = false;
                        break;
                    }
                }
            }


            if(IsLegal){
                int a = Integer.parseInt(StrContents.substring(0,6));
                int b = Integer.parseInt(StrContents.substring(7,13));
                int c = Math.abs(a-b);

                if(c > 850)
                    WarningScannerNum.setText("不合规定:共"+ c +"行(超出850)");

                else {
                    StockNumList = "";
                    for(int i = Math.min(a,b);i <= Math.max(a,b);i++) {
                        if (i == Math.min(a, b)) {
                            StockNumList += i;
                            System.out.println("------"+i);
                        }
                        else
                            StockNumList += "," + i;
                    }

                    return StockNumList;
                }
            }
            else
                return "";
        }
        else {
            WarningScannerNum.setText("格式:股号-股号,最多扫描850条");
            return "";
        }
        return "";
    }

    //获取成功扫描返回StockList条件下，ScannerLabel的提醒字符
    public void getWarningScannerResult(){
        String WarningSucAdd = "";

        if (Query.getHasNotInMarket() != 0)
            WarningSucAdd += "未上市:" + Query.getHasNotInMarket();
        if (Query.getHasExist() != 0)
            WarningSucAdd += " 已存在:" + Query.getHasExist();
        if (Query.getCurrentData().size() != 0)
            WarningSucAdd += " 成功添加:" + Query.getCurrentData().size();

        WarningScannerNum.setText(WarningSucAdd);
    }

    //获取股号对应的tableview的行号以便查找拖动
    public int getSerchToFindSTnumIndex(String stnum){
        for(int i = 0;i < observableList.size();i++){
            if(observableList.get(i).getStockNum().equals(stnum))
                return i;
        }
        return -1;
    }

    private String[] ReadSTNumList(String fileRoute,String Numlist[]){
        try {

            Numlist = new String[4];
            Numlist[0] = "";
            int i = 0;
            int readline = 0;
            File file = new File(fileRoute);
            Scanner input = new Scanner(file);
            while(input.hasNext()){
                Numlist[i] += input.nextLine();
                readline++;
                if(readline % 800 == 0) {
                    i++;
                    Numlist[i] = "";
                }
            }

            System.out.println(Numlist[0]);
            System.out.println(Numlist[1]);
            System.out.println(Numlist[2]);
            System.out.println(Numlist[3]);


            input.close();


        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            return Numlist;
        }

    }

    private void AddAllNational_STN() {
        int HistorySize = Query.getHistoryData().size();
        AddAllSH_STN();
        AddAllSZ_STN();
        WarningSerchNum.setText("成功添加" + (Query.getHistoryData().size() - HistorySize));
    }

    private void AddAllSH_STN() {

        if (Query.IsConnectBefore()) { // 连上网络
            int HistorySize = Query.getHistoryData().size();

            String a = Query.getSTMarketContents();
            Query.setSTMarketContents(",sh");
            for(int i = 0;i < 2;i++) {
                String string = shanghaiSTNumList[i];
                Query.Refresh(string);
                AddNewData_To_tableView();
            }

            WarningSerchNum.setText("成功添加" + (Query.getHistoryData().size() - HistorySize));
            Query.setSTMarketContents(a);

        }
        else
            WarningSerchNum.setText(Query.getWarningIsConnectBefore());


    }

    private void AddAllSZ_STN() {

        if (Query.IsConnectBefore()) { // 连上网络
            int HistorySize = Query.getHistoryData().size();

            String a = Query.getSTMarketContents();
            Query.setSTMarketContents(",sz");
            for(int i = 0;i < 3;i++) {
                String string = shenzhenSTNumList[i];
                Query.Refresh(string);
                AddNewData_To_tableView();
            }

            WarningSerchNum.setText("成功添加" + (Query.getHistoryData().size() - HistorySize));
            Query.setSTMarketContents(a);

        }
        else
            WarningSerchNum.setText(Query.getWarningIsConnectBefore());
    }

    private void OpenKLineStage(String STNum){


        int judgeIndex = KSmanager.IsExist(STNum);
        //-1 说明不存在 要new
        if(judgeIndex == -1) {
            Stage primaryStage = new Stage();
            KSmanager.Add(STNum,primaryStage);
            try {
                FXMLLoader fx = new FXMLLoader(getClass().getResource("../ChooseFxml/KLine.fxml"));
                fx.setController(new KLineController(STNum));
                Parent root = fx.load();
                primaryStage.setTitle(STNum + "日k线 周k线 月k线 分时线");
                primaryStage.setScene(new Scene(root, 1089, 661.0));
                primaryStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //非-1 ，则为Stage的链表中的指针号
        else{
            Stage primaryStage = KSmanager.getStage(judgeIndex);
            primaryStage.close();
            primaryStage.show();
        }

        System.out.println(KSmanager.getStageArrayList().size());

    }

    private void DeleOneSTNum(int STNumIndex,String STNum){

        //tableView.intersects();
        observableList.remove(STNumIndex);
        SDMmanager.getModelaList().remove(STNumIndex);
        Query.DeleOneHistoryData(STNum);

    }




    //------********控件监听器**************-------//


    //输入框感应手动添加动作
    public void QueryTextFieldAction(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        switch (keyCode){
            case ENTER:
                this.QueryButtonAction();
                break;
            case BACK_SPACE:
                WarningSerchNum.setText("");
                break;
        }
    }

    //手动添加键
    public void QueryButtonAction() {


        if (Query.IsConnectBefore()) { // 连上网络

            String string = QueryContents.getText();    //获取添加输入框的内容
            Query.Refresh(string);                      //对获取内容进行处理 ：历史记录的刷新 当前记录整理成合法的股号 链表。并更改提醒内容
            WarningSerchNum.setText(Query.getWarning());
            System.out.println("当前" + Query.getCurrentData());
            System.out.println("历史" + Query.getHistoryData());
            AddNewData_To_tableView();

        }
        else
            WarningSerchNum.setText(Query.getWarningIsConnectBefore());
    }

    //输入框感应扫描添加动作
    public void ScannerTextFieldAction(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        switch (keyCode){
            case ENTER:
                this.ScannerButtonAction();
                break;
            case BACK_SPACE:
                WarningScannerNum.setText("");
                break;
        }
    }

    //扫描添加键
    public void ScannerButtonAction() {

        if (Query.IsConnectBefore()) { // 连上网络

            String string = getScannerStockListNum();

            if(!string.isEmpty()) {
                Query.Refresh(string);
                AddNewData_To_tableView();
                getWarningScannerResult();
            }
        }

        else
            WarningScannerNum.setText(Query.getWarningIsConnectBefore());

    }

    //手动刷新
    public void ManuRefreshesButtonAction(){

        animatation.stop();
        WarningRefreshTime.setText("");

        if(Query.IsTimeRefresh()) {

            WarningRefreshTime.setText("");
            if (Query.IsConnectBefore()) {  // 连上网络
                RefreshTableView();
            }
            else
                WarningSerchNum.setText(Query.getWarningIsConnectBefore());

            System.out.println(Query.getHistoryData().size());
            System.out.println(SDMmanager.getModelaList().size());
            System.out.println(observableList.size());

        }

        else
            WarningRefreshTime.setText("股市交易时间:周一～五[9:30-11:30,13:00-15:00]");

    }

    //自动刷新
    public void AutoRefreshesButtonAction(){

        WarningRefreshTime.setText("");

        double seconds;

        if(SecondsContents.getText().isEmpty())
            seconds = 10;
        else
            seconds = Double.parseDouble(SecondsContents.getText());


        if(animatation != null){
            animatation.stop();
            animatation = null;
            animatation = new Timeline(new KeyFrame(Duration.seconds(seconds),event -> {
                if(Query.IsTimeRefresh()) {
                    WarningRefreshTime.setText("");
                    if (Query.IsConnectBefore()) {  // 连上网络
                        RefreshTableView();
                    }
                    else
                        WarningSerchNum.setText(Query.getWarningIsConnectBefore());
                }
                else
                    WarningRefreshTime.setText("股市交易时间:周一～五[9:30-11:30,13:00-15:00]");
            }));
            animatation.setCycleCount(Timeline.INDEFINITE);
            animatation.play();
        }
        else
            animatation.play();
    }

    //停止
    public void PauseRefreshButtonAction(){
        animatation.stop();
    }

    //清除
    public void ClearAllDataButtonAction() {
        observableList.clear();
        SDMmanager.getModelaList().clear();
        Query.getHistoryData().clear();
        tableView.setItems(observableList);
    }

    //代号查询
    public void StockNumSearchAction(KeyEvent keyEvent) {
        KeyCode keycode = keyEvent.getCode();
        switch (keycode){
            case ENTER:
                String STNum = StockNumContents.getText();
                int index = getSerchToFindSTnumIndex(STNum);
                System.out.println(index);

                //拉动跳转到具体某个位置
                if(index != -1) {
                    WarningIsExistStNum.setText("");
                    if(index >=6 )
                        tableView.scrollTo(index-6);
                    else
                        tableView.scrollTo(index);

                    tableView.getSelectionModel().select(index);
                }
                else
                    WarningIsExistStNum.setText("该股号暂未添加入列表！");

                break;
            case BACK_SPACE:
                WarningIsExistStNum.setText("");
                break;
            default:
                WarningIsExistStNum.setText("股号应输入六位纯数字");
                break;

        }
    }

    public void AddAllStNumAction() {
        int a = AllStockNumList.getSelectionModel().getSelectedIndex();
        switch (a){
            case 0:
                System.out.println("国内");
                AddAllNational_STN();
                break;
            case 1:
                System.out.println("上海");
                AddAllSH_STN();

                break;
            case 2:
                System.out.println("深圳");
                AddAllSZ_STN();
                break;
        }
    }

    public void STMarketButtonAction() {
        int a = STMarket.getSelectionModel().getSelectedIndex();

        switch (a){
            case 0:
                Query.setSTMarketContents(",sh");
                break;
            case 1:
                Query.setSTMarketContents(",sz");
                break;
        }

    }

    public void TableViewMouseClickedAction(MouseEvent mouseEvent) {

        String STNum = observableList.get(tableView.getSelectionModel().getSelectedIndex()).getStockNum();

        //双击打开K线舞台
        if (mouseEvent.getClickCount() == 2)
            OpenKLineStage(STNum);


        //右键删除
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            int StNumIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println(StNumIndex);
            DeleOneSTNum(StNumIndex,STNum);
        }


    }


    //------********控件监听器**************-------//
}
