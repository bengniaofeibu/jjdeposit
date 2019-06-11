package com.jiujiu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by t_hz on 2019/5/12.
 */
@Setter
@Getter
@Entity
@Table(name = "t_user_delay_refund")
public class UserDelayRefund {

    @Id
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "recharge_id")
    private String rechargeId;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "user_refund")
    private String userRefund;

    @Column(name = "refundState")
    private Integer refundState;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
