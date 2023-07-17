package com.pyrosandro.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorConstants {

    OK(0),
    GENERIC_ERROR(100),
    CONSTRAINT_VALIDATION_ERROR(101),
    ;

    public final int code;

}