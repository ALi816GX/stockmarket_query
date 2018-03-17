package data_deal.data_stock;

/**
 * Created by linguangxiong on 16/10/17.
 *
 * 存储DataCollect类返回的一维数组数据
 * 并将数据存储之后添加到  ObservableList上
 * 实现了模型数据替换，模型数据中的单个元素返回值（股号）
 */

public class StockDataModel {
    private String[] StockData = new String[33];    //存储数据模型的内容 32号为股票号

    //构造器
    public StockDataModel(String[] stockData) {
        this.StockData  = stockData;
    }

    //方法
    public String getstockDataCell(int index) {
        return StockData[index];
    }

    public String[] getStockData() {
        return StockData;
    }

    public void setStockData(String[] stockData) {
        StockData = stockData;
    }

    public String getStockNum() {
        return StockData[32];
    }


}
