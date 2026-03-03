package com.erp.erp.service;

import com.erp.erp.model.Compra;
import com.erp.erp.model.DetalleCompra;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.CompraRepository;
import com.erp.erp.repository.DetalleCompraRepository;
import com.erp.erp.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * CompraService robusto: aplica diffs entre compra nueva y anterior para ajustar stock correctamente.
 */
@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProductoRepository productoRepository;

    public CompraService(CompraRepository compraRepository,
                         DetalleCompraRepository detalleCompraRepository,
                         ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.productoRepository = productoRepository;
    }

    public List<Compra> listar() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorIdConDetalles(Integer id) {
        return compraRepository.findById(id).orElse(null);
    }

    /**
     * Guardar compra (nueva o edición) ajustando stock mediante diffs:
     * diff = cantidadNueva - cantidadAnterior
     *
     * Implementación defensiva:
     * - Agrupa cantidades por producto (maneja múltiples líneas del mismo producto).
     * - Calcula diffs y aplica una sola vez por producto.
     * - Evita stock negativo (ajusta a 0 y lo registra).
     * - Borra detalles antiguos antes de persistir los nuevos para evitar duplicados.
     */
    @Transactional
    public void guardarCompra(Compra compra) {
        // 0) Normalizar detalles y calcular total
        double total = 0.0;
        if (compra.getDetalles() != null) {
            for (DetalleCompra d : compra.getDetalles()) {
                if (d.getCantidad() == null) d.setCantidad(0);
                if (d.getPrecioUnitario() == null) d.setPrecioUnitario(0.0);
                if (d.getImporteTotal() == null) {
                    d.setImporteTotal(d.getPrecioUnitario() * d.getCantidad());
                }
                total += d.getImporteTotal();
            }
        }
        compra.setTotal(total);

        // 1) Map de cantidades nuevas por productoId (agrupar)
        Map<Integer, Integer> cantidadesNuevas = new HashMap<>();
        if (compra.getDetalles() != null) {
            for (DetalleCompra d : compra.getDetalles()) {
                if (d.getProducto() == null || d.getProducto().getId() == null) continue;
                Integer pid = d.getProducto().getId();
                cantidadesNuevas.put(pid, cantidadesNuevas.getOrDefault(pid, 0) + d.getCantidad());
            }
        }

        // 2) Map de cantidades anteriores por productoId (si es edición)
        Map<Integer, Integer> cantidadesAnteriores = new HashMap<>();
        Compra anterior = null;
        if (compra.getId() != null) {
            anterior = compraRepository.findById(compra.getId()).orElse(null);
            if (anterior != null && anterior.getDetalles() != null) {
                for (DetalleCompra d : anterior.getDetalles()) {
                    if (d.getProducto() == null || d.getProducto().getId() == null) continue;
                    Integer pid = d.getProducto().getId();
                    cantidadesAnteriores.put(pid, cantidadesAnteriores.getOrDefault(pid, 0) + (d.getCantidad() == null ? 0 : d.getCantidad()));
                }
            }
        }

        // 3) Conjunto de todos los productos implicados
        Set<Integer> todos = new HashSet<>();
        todos.addAll(cantidadesAnteriores.keySet());
        todos.addAll(cantidadesNuevas.keySet());

        // 4) Aplicar diffs al stock (una sola vez por producto)
        for (Integer pid : todos) {
            int oldQty = cantidadesAnteriores.getOrDefault(pid, 0);
            int newQty = cantidadesNuevas.getOrDefault(pid, 0);
            int diff = newQty - oldQty; // positivo -> aumentar stock; negativo -> reducir stock

            if (diff == 0) continue;

            Optional<Producto> optP = productoRepository.findById(pid);
            if (!optP.isPresent()) {
                logger.warn("guardarCompra: producto no encontrado id={} al aplicar diff={}", pid, diff);
                continue;
            }

            Producto p = optP.get();
            int currentStock = p.getStock() == null ? 0 : p.getStock();
            int nuevoStock = currentStock + diff;

            if (nuevoStock < 0) {
                logger.warn("guardarCompra: ajuste dejaría stock negativo para producto id={} (stock actual={}, diff={}). Ajustando a 0.",
                        pid, currentStock, diff);
                p.setStock(0);
            } else {
                p.setStock(nuevoStock);
            }

            productoRepository.save(p);
            logger.info("guardarCompra: producto id={} stock {} -> {} (diff={})", pid, currentStock, p.getStock(), diff);
        }

        // 5) Si era edición: borrar detalles antiguos para evitar duplicados
        if (anterior != null) {
            // eliminar detalles antiguos
            detalleCompraRepository.deleteByCompra(anterior);
        }

        // 6) Persistir cabecera y detalles nuevos
        Compra compraGuardada = compraRepository.save(compra);

        if (compra.getDetalles() != null) {
            for (DetalleCompra d : compra.getDetalles()) {
                d.setCompra(compraGuardada);
                detalleCompraRepository.save(d);
            }
        }
    }

    /**
     * Borrar compra: restar las cantidades de la compra del stock y eliminar registros.
     */
    @Transactional
    public void borrarCompra(Integer id) {
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra == null) return;

        if (compra.getDetalles() != null) {
            for (DetalleCompra d : compra.getDetalles()) {
                if (d.getProducto() == null || d.getProducto().getId() == null) continue;
                Optional<Producto> optP = productoRepository.findById(d.getProducto().getId());
                if (!optP.isPresent()) continue;
                Producto p = optP.get();
                int currentStock = p.getStock() == null ? 0 : p.getStock();
                int nuevoStock = currentStock - (d.getCantidad() == null ? 0 : d.getCantidad());
                if (nuevoStock < 0) {
                    logger.warn("borrarCompra: ajuste dejaría stock negativo para producto id={}. Ajustando a 0.", p.getId());
                    p.setStock(0);
                } else {
                    p.setStock(nuevoStock);
                }
                productoRepository.save(p);
                logger.info("borrarCompra: producto id={} stock {} -> {}", p.getId(), currentStock, p.getStock());
            }
        }

        detalleCompraRepository.deleteByCompra(compra);
        compraRepository.delete(compra);
    }
}
