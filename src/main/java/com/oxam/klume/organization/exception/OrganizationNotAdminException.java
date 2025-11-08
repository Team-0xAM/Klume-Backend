package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationNotAdminException extends BusinessException {
    public OrganizationNotAdminException() {
        super(ErrorCode.ORGANIZATION_NOT_ADMIN);
    }

    public OrganizationNotAdminException(final String message) {
        super(ErrorCode.ORGANIZATION_NOT_ADMIN, message);
    }
}