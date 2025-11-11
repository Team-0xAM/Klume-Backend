package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationGroupNameDuplicatedException extends BusinessException {
    public OrganizationGroupNameDuplicatedException() {
        super(ErrorCode.ORGANIZATION_GROUP_NAME_DUPLICATED);
    }

    public OrganizationGroupNameDuplicatedException(final String message) {
        super(ErrorCode.ORGANIZATION_GROUP_NAME_DUPLICATED, message);
    }
}