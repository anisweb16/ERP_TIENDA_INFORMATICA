package com.erp.erp.service;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.EscandalloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscandalloService {

    private final EscandalloRepository escandalloRepository;
    private final ProductoService productoService;

    public EscandalloService(EscandalloRepository escandalloRepository,
                             ProductoService productoService) {
        this.escandalloRepository = escandalloRepository;
        this.productoService = productoService;
    }

    public List<Escandallo> listarPorProductoCompuesto(Integer idProducto) {
        return escandalloRepository.findByProductoCompuestoIdOrderById(idProducto);
    }

    @Transactional
    public void guardarEscandallo(Producto productoCompuesto, List<Escandallo> lineas) {
        if (productoCompuesto == null || productoCompuesto.getId() == null) return;
        escandalloRepository.deleteByProductoCompuesto_Id(productoCompuesto.getId());
        if (lineas != null) {
            for (Escandallo e : lineas) {
                if (e.getComponente() == null || e.getComponente().getId() == null) continue;
                if (e.getCantidad() == null || e.getCantidad() <= 0) continue;
                e.setProductoCompuesto(productoCompuesto);
                e.setComponente(productoService.obtener(e.getComponente().getId()));
                if (e.getComponente() != null) {
                    escandalloRepository.save(e);
                }
            }
        }
    }

    @Transactional
    public void borrarPorProductoCompuesto(Integer idProducto) {
        escandalloRepository.deleteByProductoCompuesto_Id(idProducto);
    }
}
