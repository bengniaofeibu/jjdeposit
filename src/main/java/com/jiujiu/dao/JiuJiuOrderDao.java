package com.jiujiu.dao;

import com.jiujiu.model.JiuJiuOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by t_hz on 2019/5/15.
 */
public interface JiuJiuOrderDao extends JpaRepository<JiuJiuOrder,String>{

    @Modifying
    @Transactional
    @Query("UPDATE JiuJiuOrder jiujiuOrder set jiujiuOrder.state = 3 where jiujiuOrder.rechargeId = ?1")
    void updateOrderStatus(String rechargeId);

    JiuJiuOrder findByRechargeId(String rechargeId);
}
