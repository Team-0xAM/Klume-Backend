package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.member.entity.Member;

import java.util.List;

public interface FaqService {
    FaqResponse createFaq(final FaqRequest request, final Member member);

    FaqResponse updateFaq(final int faqId, final Member member, final FaqRequest request);

    void deleteFaq(final int faqId, final Member member);

    List<FaqResponse> getFaqs();

    FaqResponse getFaqDetail(final int faqId);
}
