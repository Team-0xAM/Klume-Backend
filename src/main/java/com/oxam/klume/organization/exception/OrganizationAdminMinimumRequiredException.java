package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationAdminMinimumRequiredException extends BusinessException {
    public OrganizationAdminMinimumRequiredException() {
        super(ErrorCode.ORGANIZATION_ADMIN_MINIMUM_REQUIRED);
    }

    public OrganizationAdminMinimumRequiredException(final String message) {
        super(ErrorCode.ORGANIZATION_ADMIN_MINIMUM_REQUIRED, message);
    }
}