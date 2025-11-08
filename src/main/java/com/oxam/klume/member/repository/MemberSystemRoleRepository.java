package com.oxam.klume.member.repository;

import com.oxam.klume.member.entity.MemberSystemRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSystemRoleRepository extends JpaRepository<MemberSystemRole, Integer> {
    MemberSystemRole findByMemberId(int memberId);
}
