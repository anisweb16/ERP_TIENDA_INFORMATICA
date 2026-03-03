package com.erp.erp.repository;

import com.erp.erp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    long countByStockLessThan(Integer stock);

    List<Producto> findTop8ByStockLessThanOrderByStockAsc(Integer stock);
}
