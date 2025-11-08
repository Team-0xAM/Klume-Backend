package com.oxam.klume.faq.repository;

import com.oxam.klume.faq.entity.Faq;
import com.oxam.klume.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    Optional<Member> findMemberById(int memberId);
}
