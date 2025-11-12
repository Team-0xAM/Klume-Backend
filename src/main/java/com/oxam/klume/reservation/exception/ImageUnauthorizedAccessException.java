package com.oxam.klume.reservation.exception;

import com.oxam.klume.common.error.ErrorCode;
import com.oxam.klume.common.error.exception.BusinessException;

public class ImageUnauthorizedAccessException extends BusinessException {
    public ImageUnauthorizedAccessException() {
        super(ErrorCode.IMAGE_UNAUTHORIZED_ACCESS, "회의실 이용 인증 이미지를 조회할 권한이 없습니다.");
    }
}
