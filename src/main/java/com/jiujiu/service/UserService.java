package com.jiujiu.service;

import com.jiujiu.model.User;
import com.jiujiu.utils.WebResult;

/**
 * Created by t_hz on 2019/4/16.
 */
public interface UserService {

    /**
     * 用户登录
     * @param user 用户
     * @return 用户信息
     */
    WebResult.Result doLogin(User user);

    /**
     *  获取用户列表
     * @return
     */
    WebResult.Result getUserList(int start,String phone);
}
