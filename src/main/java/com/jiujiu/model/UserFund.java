package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by t_hz on 2019/5/15.
 */

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "user")
public class UserFund {


    private String reFundUrl;

    private Integer amount;

    private Integer reFundModel;

    private String wxRefundUrl;
}
