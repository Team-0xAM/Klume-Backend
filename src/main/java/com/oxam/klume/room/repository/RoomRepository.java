package com.oxam.klume.room.repository;

import com.oxam.klume.room.entity.Room;
import com.oxam.klume.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByOrganization(Organization organization);
    Optional<Room> findByIdAndOrganization(int id, Organization organization);
    boolean existsByOrganizationAndName(Organization organization, String name);
}
