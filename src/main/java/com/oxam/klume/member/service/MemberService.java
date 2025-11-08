package com.oxam.klume.member.service;

import com.oxam.klume.member.dto.LoginRequestDTO;
import com.oxam.klume.member.dto.LoginResponseDTO;
import com.oxam.klume.member.dto.SignupRequestDTO;
import com.oxam.klume.member.dto.SignupResponseDTO;

public interface MemberService {

    SignupResponseDTO signup(SignupRequestDTO request);

    LoginResponseDTO login(LoginRequestDTO request);
}
