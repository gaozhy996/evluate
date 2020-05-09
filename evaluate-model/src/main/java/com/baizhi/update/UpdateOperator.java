package com.baizhi.update;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;

public interface UpdateOperator {
    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdateChain updateChain);
}
