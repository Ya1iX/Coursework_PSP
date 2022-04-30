package com.monitoring.attendance.repos;

import com.monitoring.attendance.enities.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Group findByName(String name);
    Iterable<Group> findByNameContaining(String name);

    @Query(value = "SELECT * FROM t_groups ORDER BY name", nativeQuery = true)
    Iterable<Group> findAllOrderByName();
}
