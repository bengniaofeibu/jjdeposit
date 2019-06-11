package com.jiujiu.dao;

import com.jiujiu.model.JiuJiuUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by t_hz on 2019/5/8.
 */
public interface JiuJiuUserDao extends JpaRepository<JiuJiuUser,String> {

    Page<JiuJiuUser> findAll(Specification specification,Pageable pageable);

    JiuJiuUser findByPhone(String phone);

    @Modifying
    @Transactional
    @Query("update JiuJiuUser jjuser set jjuser.accountStatus = ?1,jjuser.deposit = 0 where jjuser.phone = ?2")
    void updateUserStatus(Integer accountStatus,String phone);

    @Modifying
    @Transactional
    @Query("update JiuJiuUser jjuser set jjuser.accountStatus = ?1,jjuser.deposit = 0 where jjuser.id = ?2")
    void updateUserStatusById(Integer accountStatus,String userId);
}
