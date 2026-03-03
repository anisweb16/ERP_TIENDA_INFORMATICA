package com.erp.erp.controller;

import com.erp.erp.model.Cliente;
import com.erp.erp.model.DetallePedido;
import com.erp.erp.model.Pedido;
import com.erp.erp.model.Producto;
import com.erp.erp.service.ClienteService;
import com.erp.erp.service.PedidoService;
import com.erp.erp.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    private final PedidoService pedidoService;
    private final ProductoService productoService;
    private final ClienteService clienteService;

    public VentasController(PedidoService pedidoService,
                            ProductoService productoService,
                            ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer ver,
                         Model model,
                         HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        model.addAttribute("ventas", pedidoService.listar());

        if (ver != null) {
            Pedido factura = pedidoService.buscarPorIdConDetalles(ver);

            double totalFactura = 0;
            List<DetallePedido> detallesFactura = new ArrayList<>();

            if (factura != null && factura.getDetalles() != null) {
                detallesFactura = factura.getDetalles();
                for (DetallePedido d : detallesFactura) {
                    totalFactura += d.getImporteTotal();
                }
            }

            model.addAttribute("factura", factura);
            model.addAttribute("detallesFactura", detallesFactura);
            model.addAttribute("totalFactura", totalFactura);
        }

        return "ventas/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        Pedido pedido = new Pedido();
        pedido.setCliente(new Cliente());

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());

        return "ventas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Pedido pedido,
                          @RequestParam(required = false) List<Integer> productoIds,
                          @RequestParam(required = false) List<Integer> cantidades,
                          HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        if (productoIds == null) productoIds = new ArrayList<>();
        if (cantidades == null) cantidades = new ArrayList<>();

        List<DetallePedido> detalles = new ArrayList<>();

        for (int i = 0; i < productoIds.size(); i++) {

            if (i >= cantidades.size()) continue;

            Integer cantidad = cantidades.get(i);
            if (cantidad == null || cantidad <= 0) continue;

            Producto producto = productoService.obtener(productoIds.get(i));
            if (producto == null) continue;

            DetallePedido d = new DetallePedido();
            d.setProducto(producto);
            d.setCantidad(cantidad);
            d.setImporteTotal(producto.getPrecio() * cantidad);
            d.setPedido(pedido);

            detalles.add(d);
        }

        pedido.setDetalles(detalles);
        pedidoService.guardarVenta(pedido);

        return "redirect:/ventas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        Pedido pedido = pedidoService.buscarPorIdConDetalles(id);
        if (pedido == null) return "redirect:/ventas";

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());

        return "ventas/form";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id, HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        pedidoService.borrarVenta(id);
        return "redirect:/ventas";
    }
}