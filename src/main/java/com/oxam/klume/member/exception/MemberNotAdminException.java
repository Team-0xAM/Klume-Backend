package com.oxam.klume.member.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class MemberNotAdminException extends BusinessException {
    public MemberNotAdminException() {
        super(ErrorCode.MEMBER_SYSTEM_ROLE_NOT_ADMIN);
    }

    public MemberNotAdminException(final String message) {
        super(ErrorCode.MEMBER_SYSTEM_ROLE_NOT_ADMIN, message);
    }
}