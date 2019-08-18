package com.bee.scheduler.consolenode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author weiwei
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
