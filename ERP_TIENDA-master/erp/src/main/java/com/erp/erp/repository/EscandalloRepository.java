package com.erp.erp.repository;

import com.erp.erp.model.Escandallo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscandalloRepository extends JpaRepository<Escandallo, Integer> {

    List<Escandallo> findByProductoCompuestoIdOrderById(Integer idProductoCompuesto);

    void deleteByProductoCompuesto_Id(Integer idProductoCompuesto);
}
