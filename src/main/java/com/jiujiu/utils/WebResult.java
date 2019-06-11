package com.jiujiu.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by t_hz on 2019/4/16.
 */
@Setter
@Getter
public class WebResult {

    private static final Integer SUCCESS_CODE = 200;

    private static final Integer FAIL_CODE = -1;

    private static final String SUCCESS_MESSAGE = "success";

   public static Result returnSuccess(){
       Result result = new Result();
       result.setCode(SUCCESS_CODE);
       result.setMessage(SUCCESS_MESSAGE);
       return result;
   }

    public static Result returnSuccess(Object data){
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setMessage(SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static Result returnFail(String messeag){
        Result result = new Result();
        result.setCode(FAIL_CODE);
        result.setMessage(messeag);
        return result;
    }

    @Setter
    @Getter
    public static class Result{

        private Integer code;

        private String message;

        private Object data;
    }

}
