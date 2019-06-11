package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by t_hz on 2019/4/16.
 */
@Setter
@Getter
@Entity
@Table(name = "t_userinfo")
public class User {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "pass_word")
    private String passWord;

    @Transient
    private String token;

}
