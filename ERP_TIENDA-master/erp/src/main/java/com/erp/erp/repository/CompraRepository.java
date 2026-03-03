package com.erp.erp.repository;

import com.erp.erp.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Integer> {

    @Query("""
           SELECT c
           FROM Compra c
           LEFT JOIN FETCH c.detalles d
           LEFT JOIN FETCH d.producto
           WHERE c.id = :id
           """)
    Optional<Compra> findByIdWithDetalles(@Param("id") Integer id);
}