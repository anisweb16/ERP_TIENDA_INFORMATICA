package com.erp.erp.controller;

import com.erp.erp.model.Compra;
import com.erp.erp.model.DetalleCompra;
import com.erp.erp.model.Producto;
import com.erp.erp.model.Proveedor;
import com.erp.erp.service.CompraService;
import com.erp.erp.service.ProductoService;
import com.erp.erp.service.ProveedorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/compras")
public class ComprasController {

    private final CompraService compraService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    public ComprasController(CompraService compraService,
                             ProductoService productoService,
                             ProveedorService proveedorService) {
        this.compraService = compraService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    /* ================= LISTAR + FACTURA DE COMPRA ================= */

    @GetMapping
    public String listar(
            @RequestParam(required = false) Integer ver,
            Model model,
            HttpSession session) {

        if (session.getAttribute("usuario") == null)
            return "redirect:/login";

        model.addAttribute("compras", compraService.listar());

        if (ver != null) {

            Compra compraSeleccionada = compraService.buscarPorIdConDetalles(ver);

            double totalCompra = 0;
            List<DetalleCompra> detallesCompra = new ArrayList<>();

            if (compraSeleccionada != null && compraSeleccionada.getDetalles() != null) {
                detallesCompra = compraSeleccionada.getDetalles();

                for (DetalleCompra d : detallesCompra) {
                    totalCompra += d.getImporteTotal();
                }
            }

            model.addAttribute("compraSeleccionada", compraSeleccionada);
            model.addAttribute("detallesCompra", detallesCompra);
            model.addAttribute("totalCompra", totalCompra);
        }

        return "compras/list";
    }

    /* ================= NUEVA COMPRA ================= */

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Compra compra = new Compra();
        compra.setProveedor(new Proveedor());

        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("cantidadesMap", new java.util.HashMap<Integer, Integer>());

        return "compras/form";
    }

    /**
     * Guardar compra
     *
     * Recibe:
     * - compra (cabecera)
     * - productoIds: lista de ids de producto (recibidos como String para evitar errores de binding)
     * - cantidades: lista de cantidades correspondiente (recibidos como String)
     * - preciosUnitarios: lista de precios unitarios (recibidos como String)
     *
     * Construye detalles de forma segura y delega persistencia al servicio.
     */
    @PostMapping("/guardar")
    @Transactional
    public String guardar(@ModelAttribute Compra compra,
                          org.springframework.validation.BindingResult bindingResult,
                          @RequestParam(required = false) List<String> productoIds,
                          @RequestParam(required = false) List<String> cantidades,
                          @RequestParam(required = false) List<String> preciosUnitarios,
                          HttpSession session,
                          HttpServletRequest request,
                          Model model) {

        // 1) Sesión
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        // 2) Log de parámetros recibidos
        request.getParameterMap().forEach((k, v) -> System.out.println("PARAM: " + k + " = " + Arrays.toString(v)));

        // 3) Mostrar errores de binding si los hay
        if (bindingResult != null && bindingResult.hasErrors()) {
            System.out.println("BINDING ERRORS:");
            bindingResult.getFieldErrors().forEach(fe ->
                    System.out.println("BIND ERROR: field=" + fe.getField() + " rejectedValue=" + fe.getRejectedValue() + " msg=" + fe.getDefaultMessage())
            );

            // Preparar modelo para volver al formulario con datos y mensaje
            model.addAttribute("errorMessage", "Errores en los datos del formulario. Revisa los campos.");
            model.addAttribute("proveedores", proveedorService.listar());
            model.addAttribute("productos", productoService.listar());
            model.addAttribute("cantidadesMap", new java.util.HashMap<Integer, Integer>());
            return "compras/form";
        }

        // 4) Validar proveedor (asegurar que el select envía compra.proveedor.id)
        if (compra.getProveedor() == null || compra.getProveedor().getId() == null) {
            System.out.println("Falta proveedor en el objeto Compra (compra.proveedor.id).");

            model.addAttribute("errorMessage", "Selecciona un proveedor.");
            model.addAttribute("proveedores", proveedorService.listar());
            model.addAttribute("productos", productoService.listar());
            model.addAttribute("cantidadesMap", new java.util.HashMap<Integer, Integer>());
            return "compras/form";
        }
        Integer provId = compra.getProveedor().getId();
        Proveedor proveedor = proveedorService.listar()
                .stream()
                .filter(p -> p.getId() != null && p.getId().equals(provId))
                .findFirst()
                .orElse(null);
        if (proveedor == null) {
            System.out.println("Proveedor no encontrado: " + provId);

            model.addAttribute("errorMessage", "Proveedor no encontrado.");
            model.addAttribute("proveedores", proveedorService.listar());
            model.addAttribute("productos", productoService.listar());
            model.addAttribute("cantidadesMap", new java.util.HashMap<Integer, Integer>());
            return "compras/form";
        }
        compra.setProveedor(proveedor);

        // 5) Construir detalles parseando Strings de forma segura
        List<DetalleCompra> detalles = new ArrayList<>();
        double total = 0.0;

        if (productoIds == null) productoIds = new ArrayList<>();

        for (int i = 0; i < productoIds.size(); i++) {
            String pidStr = productoIds.get(i);
            Integer pid = null;
            try {
                pid = (pidStr != null && !pidStr.trim().isEmpty()) ? Integer.parseInt(pidStr) : null;
            } catch (NumberFormatException e) {
                System.out.println("ID producto inválido en índice " + i + ": '" + pidStr + "'. Saltando.");
                continue;
            }

            String cantStr = (cantidades != null && cantidades.size() > i) ? cantidades.get(i) : null;
            Integer cantidad = null;
            try {
                cantidad = (cantStr != null && !cantStr.trim().isEmpty()) ? Integer.parseInt(cantStr) : null;
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida en índice " + i + ": '" + cantStr + "'. Saltando.");
                continue;
            }

            if (pid == null || cantidad == null || cantidad <= 0) continue;

            Producto producto = productoService.obtener(pid);
            if (producto == null) {
                System.out.println("Producto no encontrado id=" + pid);
                continue;
            }

            double precioUnitario = producto.getPrecio(); // fallback
            if (preciosUnitarios != null && preciosUnitarios.size() > i) {
                String precioStr = preciosUnitarios.get(i);
                if (precioStr != null && !precioStr.trim().isEmpty()) {
                    try {
                        precioUnitario = Double.parseDouble(precioStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Precio inválido en índice " + i + ": '" + precioStr + "'. Usando fallback.");
                    }
                }
            }

            DetalleCompra d = new DetalleCompra();
            d.setProducto(producto);
            d.setCantidad(cantidad);
            d.setPrecioUnitario(precioUnitario);
            d.setImporteTotal(precioUnitario * cantidad);
            d.setCompra(compra);

            detalles.add(d);
            total += d.getImporteTotal();
        }

        compra.setDetalles(detalles);
        compra.setTotal(total);
        if (compra.getFecha() == null) compra.setFecha(new Date());

        // Persistir
        compraService.guardarCompra(compra);

        // Redirigir al listado si todo OK
        return "redirect:/compras";
    }


    /* ================= EDITAR / BORRAR ================= */

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";

        Compra compra = compraService.buscarPorIdConDetalles(id);
        if (compra == null) return "redirect:/compras";

        java.util.Map<Integer, Integer> cantidadesMap = new java.util.HashMap<>();
        if (compra.getDetalles() != null) {
            for (DetalleCompra d : compra.getDetalles()) {
                cantidadesMap.put(d.getProducto().getId(), d.getCantidad());
            }
        }

        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("cantidadesMap", cantidadesMap);

        return "compras/form";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        compraService.borrarCompra(id);
        return "redirect:/compras";
    }
}
