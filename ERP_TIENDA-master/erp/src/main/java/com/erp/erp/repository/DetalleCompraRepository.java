package com.erp.erp.repository;

import com.erp.erp.model.DetalleCompra;
import com.erp.erp.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {

    List<DetalleCompra> findByCompra(Compra compra);

    void deleteByCompra(Compra compra);

    @Modifying
    @Query("DELETE FROM DetalleCompra d WHERE d.compra.id = :compraId")
    void deleteByCompraId(@Param("compraId") Integer compraId);

    @Query("select coalesce(sum(d.importeTotal), 0) from DetalleCompra d")
    Double sumImporteTotal();
}