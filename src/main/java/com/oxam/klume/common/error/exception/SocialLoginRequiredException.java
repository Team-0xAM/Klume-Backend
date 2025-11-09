package com.oxam.klume.common.error.exception;

import com.oxam.klume.common.error.ErrorCode;

public class SocialLoginRequiredException extends BusinessException {
    public SocialLoginRequiredException() {
        super(ErrorCode.SOCIAL_LOGIN_REQUIRED);
    }
}
