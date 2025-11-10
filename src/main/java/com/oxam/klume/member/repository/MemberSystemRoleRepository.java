package com.oxam.klume.member.repository;

import com.oxam.klume.member.entity.MemberSystemRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberSystemRoleRepository extends JpaRepository<MemberSystemRole, Integer> {
    List<MemberSystemRole> findByMemberId(int memberId);
    MemberSystemRole findFirstByMemberId(int memberId);

}
