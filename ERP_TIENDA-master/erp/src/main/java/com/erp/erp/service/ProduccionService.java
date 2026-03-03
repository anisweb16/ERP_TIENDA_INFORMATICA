package com.erp.erp.service;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Fabricacion;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.EscandalloRepository;
import com.erp.erp.repository.FabricacionRepository;
import com.erp.erp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica del módulo de producción: ejecutar fabricación (actualizar stocks y registrar).
 */
@Service
public class ProduccionService {

    private final FabricacionRepository fabricacionRepository;
    private final EscandalloRepository escandalloRepository;
    private final ProductoRepository productoRepository;

    public ProduccionService(FabricacionRepository fabricacionRepository,
                             EscandalloRepository escandalloRepository,
                             ProductoRepository productoRepository) {
        this.fabricacionRepository = fabricacionRepository;
        this.escandalloRepository = escandalloRepository;
        this.productoRepository = productoRepository;
    }

    public List<Fabricacion> listarFabricaciones() {
        return fabricacionRepository.findAllByOrderByFechaDesc();
    }

    /**
     * Ejecuta una fabricación: resta componentes del escandallo y suma unidades al producto final.
     * Si el producto es simple, solo suma stock.
     */
    @Transactional
    public void ejecutarFabricacion(Fabricacion fabricacion) {
        if (fabricacion == null || fabricacion.getProducto() == null || fabricacion.getUnidades() == null || fabricacion.getUnidades() <= 0)
            return;

        Producto producto = productoRepository.findById(fabricacion.getProducto().getId()).orElse(null);
        if (producto == null) return;

        int unidades = fabricacion.getUnidades();

        if (Boolean.TRUE.equals(producto.getEsCompuesto())) {
            List<Escandallo> escandallo = escandalloRepository.findByProductoCompuestoIdOrderById(producto.getId());
            for (Escandallo e : escandallo) {
                if (e.getComponente() == null) continue;
                Producto comp = productoRepository.findById(e.getComponente().getId()).orElse(null);
                if (comp == null) continue;
                double necesario = (e.getCantidad() == null ? 0 : e.getCantidad()) * unidades;
                int restar = (int) Math.ceil(necesario);
                int stockActual = comp.getStock() == null ? 0 : comp.getStock();
                comp.setStock(Math.max(0, stockActual - restar));
                productoRepository.save(comp);
            }
        }

        int stockActual = producto.getStock() == null ? 0 : producto.getStock();
        producto.setStock(stockActual + unidades);
        productoRepository.save(producto);

        fabricacion.setProducto(producto);
        fabricacionRepository.save(fabricacion);
    }

    /**
     * Elimina una fabricación y revierte los stocks
     */
    @Transactional
    public void eliminarFabricacion(Integer idFabricacion) {
        Fabricacion fabricacion = fabricacionRepository.findById(idFabricacion).orElse(null);
        if (fabricacion == null) return;

        Producto producto = fabricacion.getProducto();
        int unidades = fabricacion.getUnidades();

        if (producto != null && unidades > 0) {
            // Restar del stock del producto fabricado
            int stockActual = producto.getStock() == null ? 0 : producto.getStock();
            producto.setStock(Math.max(0, stockActual - unidades));
            productoRepository.save(producto);

            // Si es compuesto, devolver los componentes al stock
            if (Boolean.TRUE.equals(producto.getEsCompuesto())) {
                List<Escandallo> escandallo = escandalloRepository.findByProductoCompuestoIdOrderById(producto.getId());
                for (Escandallo e : escandallo) {
                    if (e.getComponente() == null) continue;
                    Producto comp = productoRepository.findById(e.getComponente().getId()).orElse(null);
                    if (comp == null) continue;
                    double necesario = (e.getCantidad() == null ? 0 : e.getCantidad()) * unidades;
                    int sumar = (int) Math.ceil(necesario);
                    int stockComp = comp.getStock() == null ? 0 : comp.getStock();
                    comp.setStock(stockComp + sumar);
                    productoRepository.save(comp);
                }
            }
        }

        fabricacionRepository.delete(fabricacion);
    }

    public Fabricacion obtenerFabricacion(Integer id) {
        return fabricacionRepository.findById(id).orElse(null);
    }
}
