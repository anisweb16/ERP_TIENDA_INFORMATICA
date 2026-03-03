package com.erp.erp.repository;

import com.erp.erp.model.DetallePedido;
import com.erp.erp.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedido(Pedido pedido);

    void deleteByPedido(Pedido pedido);

    @Modifying
    @Query("DELETE FROM DetallePedido d WHERE d.pedido.id = :pedidoId")
    void deleteByPedidoId(@Param("pedidoId") Integer pedidoId);

    @Query("select coalesce(sum(d.importeTotal), 0) from DetallePedido d")
    Double sumImporteTotal();
}
