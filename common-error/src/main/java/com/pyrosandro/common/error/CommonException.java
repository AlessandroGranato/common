package com.pyrosandro.common.error;

import lombok.Data;

@Data
public class CommonException extends RuntimeException {

    private final ErrorConstants errorCode;
    private final Object[] errorArgs;

    public CommonException(ErrorConstants errorCode, Object[] errorArgs, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.errorCode = errorCode;
        this.errorArgs = errorArgs;
    }





}