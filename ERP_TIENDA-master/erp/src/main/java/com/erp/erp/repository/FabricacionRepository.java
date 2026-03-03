package com.erp.erp.repository;

import com.erp.erp.model.Fabricacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FabricacionRepository extends JpaRepository<Fabricacion, Integer> {

    List<Fabricacion> findAllByOrderByFechaDesc();
}
