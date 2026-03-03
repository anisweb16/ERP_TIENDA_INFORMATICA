package com.erp.erp.service;

import com.erp.erp.model.DetallePedido;
import com.erp.erp.model.Pedido;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.DetallePedidoRepository;
import com.erp.erp.repository.PedidoRepository;
import com.erp.erp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         DetallePedidoRepository detallePedidoRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorIdConDetalles(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void guardarVenta(Pedido pedido) {

        // ===== EDITAR =====
        if (pedido.getId() != null) {
            Pedido pedidoAnterior = pedidoRepository.findById(pedido.getId()).orElse(null);

            if (pedidoAnterior != null && pedidoAnterior.getDetalles() != null) {
                for (DetallePedido d : pedidoAnterior.getDetalles()) {
                    Producto p = productoRepository.findById(d.getProducto().getId()).orElse(null);
                    if (p != null) {
                        p.setStock(p.getStock() + d.getCantidad());
                        productoRepository.save(p);
                    }
                }
                detallePedidoRepository.deleteByPedido(pedidoAnterior);
            }
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        if (pedido.getDetalles() != null) {
            for (DetallePedido d : pedido.getDetalles()) {
                Producto p = productoRepository.findById(d.getProducto().getId()).orElse(null);
                if (p == null) continue;

                p.setStock(p.getStock() - d.getCantidad());
                productoRepository.save(p);

                d.setPedido(pedidoGuardado);
                detallePedidoRepository.save(d);
            }
        }
    }

    @Transactional
    public void borrarVenta(Integer id) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null || pedido.getDetalles() == null) return;

        for (DetallePedido d : pedido.getDetalles()) {
            Producto p = productoRepository.findById(d.getProducto().getId()).orElse(null);
            if (p != null) {
                p.setStock(p.getStock() + d.getCantidad());
                productoRepository.save(p);
            }
        }

        detallePedidoRepository.deleteByPedido(pedido);
        pedidoRepository.delete(pedido);
    }
}
