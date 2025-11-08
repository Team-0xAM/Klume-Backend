package com.oxam.klume.organization.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class OrganizationNoticeNotFoundException extends BusinessException {
  public OrganizationNoticeNotFoundException() {
    super(ErrorCode.ORGANIZATION_NOTICE_NOT_FOUND);
  }

  public OrganizationNoticeNotFoundException(final String message) {
    super(ErrorCode.ORGANIZATION_NOTICE_NOT_FOUND, message);
  }}
