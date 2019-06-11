package com.jiujiu.controller;

import com.jiujiu.model.JiuJiuOrder;
import com.jiujiu.model.JiuJiuUser;
import com.jiujiu.model.UserDelayRefund;
import com.jiujiu.service.OrderService;
import com.jiujiu.utils.WebResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * Created by t_hz on 2019/5/12.
 */
@RestController
@RequestMapping(value = "/order")
public class UserOrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation(value = "保存用户转账订单号",notes = "保存用户转账订单号")
    @PostMapping(value = "/save/orderNum")
    @CrossOrigin
    public WebResult.Result saveReturnOrderNum(String orderNum,String userId){
        orderService.saveReturnOrderNum(orderNum,userId);
        return WebResult.returnSuccess();
    }

    @GetMapping(value = "/get/orderList")
    @CrossOrigin
    public WebResult.Result selectUserReturnInfoList(int start,String orderNum){
        UserDelayRefund userDelayRefund = new UserDelayRefund();
        userDelayRefund.setRechargeId(orderNum);
        WebResult.Result result = orderService.selectUserReturnInfoList(start, userDelayRefund);
        return result;
    }

    @PostMapping(value = "/update/orderDate")
    @CrossOrigin
    public WebResult.Result updateReturnDate(@RequestBody JiuJiuOrder jiuJiuOrder){
        orderService.advanceRefund(jiuJiuOrder.getRechargeId());
        return WebResult.returnSuccess();
    }

    @PostMapping(value = "/user/refund")
    @CrossOrigin
    public WebResult.Result userReFund(@RequestBody JiuJiuUser jiuJiuUser){
        return orderService.userReFund(jiuJiuUser.getPhone());
    }

    @PostMapping(value = "/user/wxRefund")
    @CrossOrigin
    public WebResult.Result userWxReFund(@RequestBody JiuJiuOrder jiuJiuOrder){
        return orderService.wxRefund(jiuJiuOrder);
    }


    @PostMapping(value = "/update/orderStatus")
    @CrossOrigin
    public WebResult.Result updateUserOrderStatus(@RequestBody JiuJiuOrder jiuJiuOrder){
        orderService.updateUserOrderStatus(jiuJiuOrder);
        return WebResult.returnSuccess();
    }

    @PostMapping(value = "/upload")
    @CrossOrigin
    public WebResult.Result uploadExcel(HttpServletRequest request,@RequestParam("excelFile") MultipartFile multipartFile) throws Exception {

        if (multipartFile.isEmpty()) {
            return WebResult.returnFail("文件不能为空");
        }
        InputStream inputStream = multipartFile.getInputStream();
        List<List<Object>> list = orderService.getBankListByExcel(inputStream, multipartFile.getOriginalFilename());
        inputStream.close();

        for (int i = 0; i < list.size()-1; i++) {
            List<Object> lo = list.get(i);
            if (CollectionUtils.isEmpty(lo) || lo.get(0).toString().equals("")){
                continue;
            }
            JiuJiuOrder jiuJiuOrder = new JiuJiuOrder();
            jiuJiuOrder.setRechargeId(lo.get(0).toString());
            jiuJiuOrder.setUserRefund(lo.get(1).toString());
            orderService.updateUserOrderStatus(jiuJiuOrder);

        }
        return WebResult.returnSuccess();
    }
}
