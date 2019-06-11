package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by t_hz on 2019/6/11.
 */
@Setter
@Getter
public class WxRefundResult {

    private Integer code;

    private String msg;

    private Object data;
}
