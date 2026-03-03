package com.erp.erp.repository;

import com.erp.erp.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @Query("""
           SELECT p
           FROM Pedido p
           LEFT JOIN FETCH p.detalles d
           LEFT JOIN FETCH d.producto
           WHERE p.id = :id
           """)
    Optional<Pedido> findByIdWithDetalles(@Param("id") Integer id);
}
