package com.github.jaxing.common.exception;

/**
 * @author cjxin
 * @date 2023/10/18
 */
public class ServiceException extends RuntimeException {

    private final String errorMsg;

    public ServiceException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
