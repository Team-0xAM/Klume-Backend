package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationNotFoundException extends BusinessException {
    public OrganizationNotFoundException() {
        super(ErrorCode.ORGANIZATION_NOT_FOUND);
    }

    public OrganizationNotFoundException(final String message) {
        super(ErrorCode.ORGANIZATION_NOT_FOUND, message);
    }
}