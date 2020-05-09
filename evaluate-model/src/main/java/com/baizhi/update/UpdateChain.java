package com.baizhi.update;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;

import java.util.List;

public class UpdateChain {
    private List<UpdateOperator> operators;
    private Integer pos;
    private Integer size;

    public UpdateChain(List<UpdateOperator> operators) {
        this.operators = operators;
        this.pos = 0;
        this.size = operators.size();
    }

    public void doChain(LoginSuccessData loginSuccessData, HistoryData historyData){
        if(pos < size){
            UpdateOperator operator = operators.get(pos);
            pos ++;
            operator.invoke(loginSuccessData,historyData,this);
        }
    }

    public void reset() {
        pos = 0;
    }
}
