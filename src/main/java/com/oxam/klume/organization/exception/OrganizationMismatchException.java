package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationMismatchException extends BusinessException {
    public OrganizationMismatchException() {
        super(ErrorCode.ORGANIZATION_MISMATCH);
    }

    public OrganizationMismatchException(final String message) {
        super(ErrorCode.ORGANIZATION_MISMATCH, message);
    }
}