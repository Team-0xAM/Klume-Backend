package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface FaqService {
    FaqResponse createFaq(final FaqRequest request, final int memberId);

    FaqResponse updateFaq(final int faqId,final int memberId, final FaqRequest request);

    void deleteFaq(final int faqId, final int memberId);

    List<FaqResponse> getFaqs();

    FaqResponse getFaqDetail(final int faqId);
}
