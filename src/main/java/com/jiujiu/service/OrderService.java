package com.jiujiu.service;

import com.jiujiu.model.JiuJiuOrder;
import com.jiujiu.model.UserDelayRefund;
import com.jiujiu.utils.WebResult;

import java.io.InputStream;
import java.util.List;

/**
 * Created by t_hz on 2019/5/12.
 */
public interface OrderService {

    void saveReturnOrderNum(String userId,String orderNum);

    WebResult.Result selectUserReturnInfoList(int start,UserDelayRefund userDelayRefund);

    void advanceRefund(String orderId);

    WebResult.Result userReFund(String phone,String state);

    void updateUserOrderStatus(JiuJiuOrder jiuJiuOrder);

    List getBankListByExcel(InputStream in, String fileName) throws Exception;


    WebResult.Result wxRefund(JiuJiuOrder jiuJiuOrder);
}
