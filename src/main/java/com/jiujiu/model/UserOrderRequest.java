package com.jiujiu.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by t_hz on 2019/5/12.
 */
@Setter
@Getter
@ApiModel("用户订单请求类")
public class UserOrderRequest {

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户转账单号")
    private String orderNum;
}
