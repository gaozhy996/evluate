package com.baizhi.update;

import com.baizhi.entity.HistoryData;
import com.baizhi.entity.LoginSuccessData;

/**
 * 所有更新组件的接口
 */
public interface UpdateOperator {

    public void invoke(LoginSuccessData loginSuccessData, HistoryData historyData, UpdaterChain chain);
}
