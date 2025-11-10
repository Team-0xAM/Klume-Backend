package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationInvitationCodeInvalidException extends BusinessException {
    public OrganizationInvitationCodeInvalidException() {
        super(ErrorCode.ORGANIZATION_INVITATION_CODE_INVALID);
    }

    public OrganizationInvitationCodeInvalidException(final String message) {
        super(ErrorCode.ORGANIZATION_INVITATION_CODE_INVALID, message);
    }
}