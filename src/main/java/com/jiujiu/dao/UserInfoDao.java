package com.jiujiu.dao;

import com.jiujiu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by t_hz on 2019/5/8.
 */
public interface UserInfoDao extends JpaRepository<User,String> {


    User findByUserNameAndPassWord(String userName,String passWord);

}
