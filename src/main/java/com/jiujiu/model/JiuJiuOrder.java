package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by t_hz on 2019/5/15.
 */
@Setter
@Getter
@Entity
@Table(name = "t_amount_record")
public class JiuJiuOrder {

    @Id
    private String id;

    @Column(name = "recharge_id")
    private String rechargeId;

    @Transient
    private String phone;

    @Transient
    private Integer accountStatus;

    @Transient
    private String userRefund;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "state")
    private Integer state;

}
