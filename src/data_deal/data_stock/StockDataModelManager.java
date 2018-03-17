package data_deal.data_stock;

import java.util.ArrayList;

/**
 * Created by linguangxiong on 16/10/17.
 * 单例类：负责管理所有添加的StockDataModel 有利于刷新数据
 * 实现了删除 添加的方法
 * 实现了刷新的方法
 */


public class StockDataModelManager {
    private static StockDataModelManager stockDataCollectManager;
    private ArrayList<StockDataModel> ModelaList = new ArrayList();

    private StockDataModelManager() {

    }

    //单例类赋给同一个对象
    public static StockDataModelManager getInstance(){
        if(stockDataCollectManager==null){
            stockDataCollectManager = new StockDataModelManager();
        }
        return stockDataCollectManager;
    }

    //删除
    public void remove(int i){
        this.ModelaList.remove(i);
    }
    //添加
    public void add(StockDataModel a){
        this.ModelaList.add(a);
    }

    //刷新模型的内容的内容
    public void RefreshAllDataModel(ArrayList<String[]> NewModelList){
        for(int i = 0;i < this.ModelaList.size();i++) {
            String[] New = NewModelList.get(i);
            this.ModelaList.get(i).setStockData(New);
        }
    }

    //返回ModelData管理列表的表
    public ArrayList<StockDataModel> getModelaList() {
        return ModelaList;
    }



}
