package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by t_hz on 2019/5/8.
 */
@Setter
@Getter
@Entity
@Table(name = "t_user_info")
public class JiuJiuUser {

    @Id
    private String id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "account_status")
    private Integer accountStatus;

    @Column(name = "deposit")
    private Double deposit;
}
