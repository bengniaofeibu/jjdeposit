package com.jiujiu.service.impl;

import com.google.common.collect.Maps;
import com.jiujiu.dao.JiuJiuOrderDao;
import com.jiujiu.dao.JiuJiuUserDao;
import com.jiujiu.dao.UserOrderDao;
import com.jiujiu.model.*;
import com.jiujiu.service.OrderService;
import com.jiujiu.utils.HttpClientUtil;
import com.jiujiu.utils.JsonUtil;
import com.jiujiu.utils.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by t_hz on 2019/5/12.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static ConcurrentMap<String, Date> USER_DELAY_REFUND_MAP = new ConcurrentHashMap<>();

    private static String USER_DELAY_REFUND_KEY = "userDelayRefundMap";

    @Resource
    private UserOrderDao userOrderDao;

    @Resource
    private JiuJiuUserDao jiuJiuUserDao;

    @Resource
    private JiuJiuOrderDao jiuJiuOrderDao;

    @Resource
    private UserFund userFund;


    @Override
    public void saveReturnOrderNum(String orderNum, String userId) {
        if (StringUtils.isEmpty(orderNum)){
            return;
        }

        //记录用户转账单号
        userOrderDao.saveReturnOrderNum(orderNum,userId);

    }

    @Override
    public WebResult.Result selectUserReturnInfoList(int start, UserDelayRefund userDelayRefund) {
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        Pageable pageable = new PageRequest(start-1,20,sort);
        List<Predicate> predicateList = new LinkedList<>();
        Specification<UserDelayRefund> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            Predicate refundState = criteriaBuilder.equal(root.get("refundState").as(Integer.class), 0);
            predicateList.add(refundState);
            if(!StringUtils.isEmpty(userDelayRefund.getRechargeId())){
                Predicate predicate = criteriaBuilder.equal(root.get("rechargeId").as(String.class),userDelayRefund.getRechargeId());
                predicateList.add(predicate);
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            Predicate[] predicatesArray = predicateList.toArray(predicates);
            return criteriaBuilder.and(predicatesArray);
        });

        Page<UserDelayRefund> userPage = userOrderDao.findAll(specification,pageable);
        List<UserDelayRefund> userDelayRefundList = userPage.getContent();
        if (!CollectionUtils.isEmpty(userDelayRefundList) && StringUtils.isEmpty(userDelayRefund.getRechargeId())){
            USER_DELAY_REFUND_MAP.put(USER_DELAY_REFUND_KEY, userDelayRefundList.get(0).getCreateTime());
        }
        return WebResult.returnSuccess(new UserOrderResponse(userPage.getContent(),userPage.getTotalElements()));
    }

    @Override
    public void advanceRefund(String orderId) {

        if (StringUtils.isEmpty(orderId)){
            return;
        }

        Date date = USER_DELAY_REFUND_MAP.get(USER_DELAY_REFUND_KEY);
        if (date != null){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH,-1);
            userOrderDao.updateReturnDate(c.getTime(),c.getTime(),orderId);
        }
    }

    /**
     * 用户退款
     * @param phone
     * @return
     */
    @Override
    public WebResult.Result userReFund(String phone,String state) {

        JiuJiuUser userInfo = jiuJiuUserDao.findByPhone(phone);

        if (userInfo == null){
            return WebResult.returnFail("申请退款失败");
        }

        Map param = Maps.newHashMap();
        param.put("amount",new BigDecimal(userFund.getAmount()));
        param.put("rechargeMode",String.valueOf(userFund.getReFundModel()));
        param.put("userId",userInfo.getId());
        param.put("state",state);
        String toJSONString = JsonUtil.toJSONString(param);
        String s = HttpClientUtil.httpPostWithJSON(userFund.getReFundUrl(), toJSONString);
        log.debug("用户退款信息 {}",s);

        if (s.equals("-1")){
            return WebResult.returnFail("申请退款失败");
        }

        BackResult backResult = JsonUtil.parseObject(s, BackResult.class);
        if ("1".equals(state) && backResult != null
                              && backResult.getCode() != null
                              && (200 == backResult.getCode() || 3 == backResult.getCode())){

            Integer userStatus = 0;
            switch (userInfo.getAccountStatus()){
                case 1:
                    userStatus = 0;
                    break;
                case 3:
                    userStatus = 2;
                    break;
            }

            //更新用户表的信息
            jiuJiuUserDao.updateUserStatus(userStatus,userInfo.getPhone());

        }

        return WebResult.returnSuccess(backResult);
    }

    @Override
    public void updateUserOrderStatus(JiuJiuOrder jiuJiuOrder) {

        if (StringUtils.isEmpty(jiuJiuOrder.getRechargeId())){
            return;
        }

          //更新用户订单表的状态
         jiuJiuOrderDao.updateOrderStatus(jiuJiuOrder.getRechargeId());

         JiuJiuOrder jiuOrder = jiuJiuOrderDao.findByRechargeId(jiuJiuOrder.getRechargeId());

        if (jiuOrder == null){
            return;
        }

        //更新用户延迟退款表的信息
        userOrderDao.updateUserOrderStatus(jiuOrder.getRechargeId(),jiuJiuOrder.getUserRefund());

        Optional<JiuJiuUser> byId = jiuJiuUserDao.findById(jiuOrder.getUserId());
         JiuJiuUser jiuJiuUser = byId.get();

        Integer userStatus = jiuJiuUser.getAccountStatus();
        if (null == userStatus  || 2 == userStatus || 0 == userStatus){
            return;
        }

         switch (jiuJiuUser.getAccountStatus()){
            case 1:
                userStatus = 0;
            break;
            case 3:
                userStatus = 2;
            break;
         }

         //更新用户表的信息
         jiuJiuUserDao.updateUserStatus(userStatus,jiuJiuUser.getPhone());


    }

    /**
     * 处理上传的文件
     *
     * @param in
     * @param fileName
     * @return
     * @throws Exception
     */
    @Override
    public List getBankListByExcel(InputStream in, String fileName) throws Exception {
        List list = new ArrayList<>();
        //创建Excel工作薄
        Workbook work = this.getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }

                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
//                    String cellValue = getCellValue(cell);
//                    cell.setCellValue(cellValue);
                    li.add(cell);
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 微信转账
     * @param jiuJiuOrder
     * @return
     */
    @Override
    public WebResult.Result wxRefund(JiuJiuOrder jiuJiuOrder) {

        JiuJiuOrder jiuOrder = jiuJiuOrderDao.findByRechargeId(jiuJiuOrder.getRechargeId());


        JiuJiuUser jiuJiuUser = jiuJiuUserDao.findById(jiuOrder.getUserId()).get();

        int accountStatus = 0;
        switch (jiuJiuUser.getAccountStatus()){
            case 0:
                accountStatus = 1;
            break;
            case 2:
                accountStatus = 3;
            break;
        }

        jiuJiuUserDao.updateUserStatusById(accountStatus,jiuOrder.getUserId());

        Map param = Maps.newHashMap();
        param.put("userId",jiuOrder.getUserId());
        String toJSONString = JsonUtil.toJSONString(param);
        String s = HttpClientUtil.httpPostWithJSON(userFund.getWxRefundUrl(), toJSONString);
        log.debug("用户微信转账信息 {}",s);

        if (s.equals("-1")){
            return WebResult.returnFail("用户微信转账失败");
        }

        WxRefundResult backResult = JsonUtil.parseObject(s, WxRefundResult.class);

        if (backResult != null && backResult.getCode().equals(1105) ){
            userOrderDao.updateWxReturnStatus(jiuJiuOrder.getRechargeId());
        }

        jiuJiuUserDao.updateUserStatusById(jiuJiuUser.getAccountStatus(),jiuOrder.getUserId());

        return WebResult.returnSuccess(backResult);
    }

    /**
     * 判断文件格式
     *
     * @param inStr
     * @param fileName
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook workbook = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            workbook = new HSSFWorkbook(inStr);
        } else if (".xlsx".equals(fileType)) {
            workbook = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("请上传excel文件！");
        }
        return workbook;
    }

    private static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: // 数字
                //short s = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    // 验证short值
                    if (cell.getCellStyle().getDataFormat() == 14) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (cell.getCellStyle().getDataFormat() == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (cell.getCellStyle().getDataFormat() == 22) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else {
                        throw new RuntimeException("日期格式错误!!!");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 0) {//处理数值格式
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                }
                break;
            case Cell.CELL_TYPE_STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: // 空值
                cellValue = null;
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
