package com.jiujiu.dao;

import com.jiujiu.model.UserDelayRefund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by t_hz on 2019/5/12.
 */
public interface UserOrderDao extends JpaRepository<UserDelayRefund,Integer>{


    @Modifying
    @Transactional
    @Query("UPDATE UserDelayRefund refund set refund.userAccount=?1 where refund.userId = ?2")
    void saveReturnOrderNum(String userAccount,String userId);

    @Modifying
    @Transactional
    @Query("update UserDelayRefund refund set refund.refundState = 3,refund.userRefund = ?2 where refund.rechargeId = ?1 ")
    void updateUserOrderStatus(String rechargeId,String userRefund);


    @Modifying
    @Transactional
    @Query("UPDATE UserDelayRefund refund set refund.updateTime=?1,refund.createTime=?2 where refund.rechargeId = ?3")
    void updateReturnDate(Date updateTime,Date createTime,String rechargeId);

    @Modifying
    @Transactional
    @Query("UPDATE UserDelayRefund refund set refund.refundState=3 where refund.rechargeId = ?1")
    void updateWxReturnStatus(String rechargeId);

    Page<UserDelayRefund> findAll(Specification<UserDelayRefund> specification, Pageable pageable);
}
