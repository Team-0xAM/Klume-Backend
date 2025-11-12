package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationMemberBannedException extends BusinessException {
    public OrganizationMemberBannedException() {
        super(ErrorCode.ORGANIZATION_MEMBER_BANNED);
    }
}