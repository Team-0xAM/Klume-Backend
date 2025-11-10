package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationMemberAccessDeniedException extends BusinessException {
    public OrganizationMemberAccessDeniedException() {
        super(ErrorCode.ORGANIZATION_MEMBER_ACCESS_DENIED);
    }

    public OrganizationMemberAccessDeniedException(final String message) {
        super(ErrorCode.ORGANIZATION_MEMBER_ACCESS_DENIED, message);
    }
}