package ChooseFxml;

import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by linguangxiong on 2016/10/31.
 */
public class KLineStageManager {
    private static KLineStageManager kLineStageManager;
    private ArrayList<Stage> StageArrayList = new ArrayList();
    private ArrayList<String>  StNumArrayList = new ArrayList<>();

    private KLineStageManager(){

    }

    public static KLineStageManager getInstance(){
        if(kLineStageManager==null){
            kLineStageManager = new KLineStageManager();
        }
        return kLineStageManager;
    }

    public void Add(String StNum,Stage stage){
        StNumArrayList.add(StNum);
        StageArrayList.add(stage);
    }


    public int IsExist(String a){
        for(int i = 0;i < StNumArrayList.size();i++)
            if(StNumArrayList.get(i).equals(a))
                return i;
        return -1;
    }

    public Stage getStage(int i){
        return StageArrayList.get(i);
    }

    public ArrayList<Stage> getStageArrayList() {
        return StageArrayList;
    }

}
