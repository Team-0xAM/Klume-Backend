package com.oxam.klume.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailVerifyRequestDTO {
    private String email;
    private String code;
}
