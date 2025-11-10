package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
