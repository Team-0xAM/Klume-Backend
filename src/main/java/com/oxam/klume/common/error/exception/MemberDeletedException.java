package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class MemberDeletedException extends BusinessException {
    public MemberDeletedException() {
        super(ErrorCode.MEMBER_DELETED);
    }
}
