package com.oxam.klume.common.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 프로필 이미지 관련 서비스
 */
@Service
public class ProfileImageServiceImpl implements ProfileImageService {

    private static final int PROFILE_IMAGE_COUNT = 4;  // 1.png ~ 4.png
    private static final String PROFILE_IMAGE_BASE_URL = "/profile/";
    private final Random random = new Random();

    /**
     * 랜덤 프로필 이미지 URL 반환
     * @return /profile/1.png ~ /profile/4.png 중 랜덤
     */
    public String getRandomProfileImageUrl() {
        int randomNumber = random.nextInt(PROFILE_IMAGE_COUNT) + 1;  // 1 ~ 4
        return PROFILE_IMAGE_BASE_URL + randomNumber + ".png";
    }
}
