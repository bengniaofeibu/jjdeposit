package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by t_hz on 2019/5/12.
 */
@Setter
@Getter
public class UserOrderResponse {

    private List<UserDelayRefund> userDelayRefundList;

    private Long totalNum;

    public UserOrderResponse(List<UserDelayRefund> userDelayRefundList,Long totalNum){
        this.userDelayRefundList = userDelayRefundList;
        this.totalNum = totalNum;
    }
}
