package com.oxam.klume.member.repository;

import com.oxam.klume.member.entity.SystemRole;
import com.oxam.klume.member.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemRoleRepository extends JpaRepository<SystemRole, Integer> {
    Optional<SystemRole> findByName(Role name);
}
