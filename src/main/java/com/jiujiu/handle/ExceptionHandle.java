package com.jiujiu.handle;

import com.jiujiu.utils.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by t_hz on 2019/5/12.
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public WebResult.Result handleEx(Exception e){
        log.error("赳赳微服务内部错误 {}",e.getMessage(),e);
         return WebResult.returnFail("服务内部错误");
    }
}
