package com.jiujiu.controller;

import com.jiujiu.model.User;
import com.jiujiu.service.UserService;
import com.jiujiu.utils.WebResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by t_hz on 2019/4/16.
 */
@RestController
@RequestMapping(value = "/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping(value = "/doLogin")
    public WebResult.Result doLogin(@RequestBody User user){

         WebResult.Result result = userService.doLogin(user);

         User userInfo  = (User)result.getData();

          return result;
    }

    @GetMapping(value = "/get/userList")
    public WebResult.Result getUserList(int start,String phone){
        return userService.getUserList(start,phone);
    }
}
