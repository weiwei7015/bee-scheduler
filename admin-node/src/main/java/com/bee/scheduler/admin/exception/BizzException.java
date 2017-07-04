package com.bee.scheduler.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

/**
 * @author weiwei 业务异常，用于捕获异常时做特殊处理
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BizzException extends RuntimeException {
    public static final int error_code_unknown = 101;
    public static final int error_code_require_login = 102;
    public static final int error_code_invalid_params = 103;
    public static final int error_code_incomplete_params = 104;
    public static final int error_code_invalid_request = 105;
    public static final int error_code_invalid_sign = 106;
    public static final int error_code_unauthorized = 107;
    private static HashMap<Integer, String> errorMsgMap = new HashMap<Integer, String>() {
        {
            put(error_code_unknown, "系统太忙，请稍后再试");
            put(error_code_require_login, "请先登录");
            put(error_code_invalid_params, "请求参数有误");
            put(error_code_incomplete_params, "缺失必选参数");
            put(error_code_invalid_request, "非法请求");
            put(error_code_invalid_sign, "非法签名");
            put(error_code_unauthorized, "接口访问权限受限");
        }
    };
    private Integer code;
    private Object data = new Object();

    public BizzException(Integer code) {
        this(code, errorMsgMap.get(code));
    }

    public BizzException(Integer code, Object data) {
        this(code, errorMsgMap.get(code), data);
    }

    public BizzException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizzException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
