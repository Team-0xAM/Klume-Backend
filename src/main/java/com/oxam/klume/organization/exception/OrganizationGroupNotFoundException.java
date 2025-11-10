package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationGroupNotFoundException extends BusinessException {
    public OrganizationGroupNotFoundException() {
        super(ErrorCode.ORGANIZATION_GROUP_NOT_FOUND);
    }

    public OrganizationGroupNotFoundException(final String message) {
        super(ErrorCode.ORGANIZATION_GROUP_NOT_FOUND, message);
    }
}