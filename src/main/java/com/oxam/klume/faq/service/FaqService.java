package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;

public interface FaqService {
    FaqResponse createFaq(FaqRequest request, int memberId);
}
