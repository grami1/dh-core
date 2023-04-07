package com.grami1.dhcore.domain.repository;

import com.grami1.dhcore.domain.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findAllByUserId(long userId);
}