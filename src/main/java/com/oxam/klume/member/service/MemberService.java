package com.oxam.klume.member.service;

import com.oxam.klume.member.dto.SignupRequest;
import com.oxam.klume.member.dto.SignupResponse;

public interface MemberService {

    SignupResponse signup(SignupRequest request);
}
