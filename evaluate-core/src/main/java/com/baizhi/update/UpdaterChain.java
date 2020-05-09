package com.baizhi.update;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;

import java.util.List;

/**
 * 更新器链路对象
 */
public class UpdaterChain {
    // 更新器组件列表
    private List<UpdateOperator> operators;
    // 位置指针
    private int position;

    public UpdaterChain(List<UpdateOperator> operators) {
        this.operators = operators;
        position = 0;
    }

    public void doUpdate(LoginSuccessData loginSuccessData, HistoryData historyData) {
        if(operators != null && position < operators.size()){
            UpdateOperator operator = operators.get(position);
            position ++;
            operator.invoke(loginSuccessData,historyData,this);
        }
    }

    public void reset(){
        position = 0;
    }
}
