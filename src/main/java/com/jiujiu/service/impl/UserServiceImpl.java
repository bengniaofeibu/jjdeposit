package com.jiujiu.service.impl;

import com.jiujiu.dao.JiuJiuUserDao;
import com.jiujiu.dao.UserInfoDao;
import com.jiujiu.model.JiuJiuUser;
import com.jiujiu.model.JiuJiuUserResVO;
import com.jiujiu.model.JiuJiuUserResponse;
import com.jiujiu.model.User;
import com.jiujiu.service.UserService;
import com.jiujiu.utils.UUIdUtil;
import com.jiujiu.utils.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by t_hz on 2019/4/16.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private JiuJiuUserDao jiuJiuUserDao;

    /**
     * 用户登录
     *
     * @param user 用户
     * @return 用户信息
     */
    @Override
    public WebResult.Result doLogin(User user) {
        log.debug("用户登录 用户名 {} 密码 {} ",user.getUserName(),user.getPassWord());
      User userInfo = userInfoDao.findByUserNameAndPassWord(user.getUserName(),user.getPassWord());
        if (userInfo == null){
            return WebResult.returnFail("用户名或密码错误");
        }
        userInfo.setToken(UUIdUtil.getUuId());
        return WebResult.returnSuccess(userInfo);
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @Override
    public WebResult.Result getUserList(int start,String phone) {
        Pageable pageable = new PageRequest(start-1,20);

        Specification<JiuJiuUser> specification = (((root, criteriaQuery, criteriaBuilder) -> {

            if (!StringUtils.isEmpty(phone)){
                Predicate userPhone = criteriaBuilder.equal(root.get("phone").as(String.class), phone);
                return criteriaBuilder.and(userPhone);
            }

            return criteriaBuilder.conjunction();

        }));

        Page<JiuJiuUser> userPage = jiuJiuUserDao.findAll(specification,pageable);

        List<JiuJiuUser> userList = userPage.getContent();

        List<JiuJiuUserResVO> jiuJiuUserResVOs = new LinkedList<>();
        userList.forEach(jiuJiuUser -> {
            JiuJiuUserResVO jiuJiuUserResVO = new JiuJiuUserResVO();
            jiuJiuUserResVO.setPhone(jiuJiuUser.getPhone());
            jiuJiuUserResVO.setDeposit(jiuJiuUser.getDeposit());

            Integer accountStatus = jiuJiuUser.getAccountStatus();

            String userStatus = null;
            switch (accountStatus){
                case 0:
                  userStatus = "无押金无实名";
                break;
                case 1:
                  userStatus = "有押金无实名";
                 break;
                case 2:
                  userStatus = "实名无押金";
                break;
                case 3:
                  userStatus = "实名有押金";
                break;
            }
            jiuJiuUserResVO.setUserStatus(userStatus);
            jiuJiuUserResVOs.add(jiuJiuUserResVO);
        });

        return WebResult.returnSuccess(new JiuJiuUserResponse(jiuJiuUserResVOs,userPage.getTotalElements()));
    }
}
