package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationMemberNotFoundException extends BusinessException {
    public OrganizationMemberNotFoundException() {
        super(ErrorCode.ORGANIZATION_MEMBER_NOT_FOUNT);
    }

    public OrganizationMemberNotFoundException(final String message) {
        super(ErrorCode.ORGANIZATION_MEMBER_NOT_FOUNT, message);
    }
}