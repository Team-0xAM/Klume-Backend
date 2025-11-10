package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationMemberAlreadyExistsException extends BusinessException {
    public OrganizationMemberAlreadyExistsException() {
        super(ErrorCode.ORGANIZATION_MEMBER_ALREADY_EXISTS);
    }

    public OrganizationMemberAlreadyExistsException(final String message) {
        super(ErrorCode.ORGANIZATION_MEMBER_ALREADY_EXISTS, message);
    }
}