package com.erp.erp.controller;

import com.erp.erp.model.Usuario;
import com.erp.erp.model.Producto;
import com.erp.erp.model.Pedido;
import com.erp.erp.service.*;
import com.erp.erp.repository.DetallePedidoRepository;
import com.erp.erp.repository.DetalleCompraRepository;
import com.erp.erp.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired private ClienteService clienteService;
    @Autowired private ProveedorService proveedorService;
    @Autowired private ProductoService productoService;
    @Autowired private EmpleadoService empleadoService;
    @Autowired private PedidoService pedidoService;
    @Autowired private CompraService compraService;

    @Autowired private DetallePedidoRepository detallePedidoRepository;
    @Autowired private DetalleCompraRepository detalleCompraRepository;
    @Autowired private ProductoRepository productoRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        // ====== KPIs (datos reales desde tu BDD) ======
        int totalClientes = clienteService.listar().size();
        int totalProveedores = proveedorService.listar().size();
        int totalProductos = productoService.listar().size();
        int totalEmpleados = empleadoService.listar().size();
        int totalVentas = pedidoService.listar().size();
        int totalCompras = compraService.listar().size();

        double ingresos = Optional.ofNullable(detallePedidoRepository.sumImporteTotal()).orElse(0.0);
        double gastos = Optional.ofNullable(detalleCompraRepository.sumImporteTotal()).orElse(0.0);
        double beneficio = ingresos - gastos;

        long stockBajo = productoRepository.countByStockLessThan(5);
        List<Producto> productosStockBajo = productoRepository.findTop8ByStockLessThanOrderByStockAsc(5);

        // ====== Chart: estado de ventas (conteo por estado) ======
        List<Pedido> ventas = pedidoService.listar();
        Map<String, Long> estadoVentaMap = ventas.stream()
                .collect(Collectors.groupingBy(v -> {
                    String e = (v.getEstado() == null || v.getEstado().isBlank()) ? "SIN_ESTADO" : v.getEstado().toUpperCase();
                    return e;
                }, Collectors.counting()));

        List<String> estadoVentaLabels = new ArrayList<>(estadoVentaMap.keySet());
        Collections.sort(estadoVentaLabels);
        List<Long> estadoVentaValues = estadoVentaLabels.stream().map(estadoVentaMap::get).collect(Collectors.toList());

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProveedores", totalProveedores);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalEmpleados", totalEmpleados);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("ingresos", ingresos);
        model.addAttribute("gastos", gastos);
        model.addAttribute("beneficio", beneficio);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("estadoVentaLabels", estadoVentaLabels);
        model.addAttribute("estadoVentaValues", estadoVentaValues);

        return "dashboard";
    }
}
