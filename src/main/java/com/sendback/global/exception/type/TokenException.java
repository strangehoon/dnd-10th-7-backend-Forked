package com.sendback.global.exception.type;

import com.sendback.global.exception.BaseException;
import com.sendback.global.exception.ExceptionType;

public class TokenException extends BaseException {
    public TokenException(ExceptionType exceptionType){
        super(exceptionType);
    }
}