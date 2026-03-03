package com.erp.erp.controller;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Fabricacion;
import com.erp.erp.model.Producto;
import com.erp.erp.service.EscandalloService;
import com.erp.erp.service.EmpleadoService;
import com.erp.erp.service.ProduccionService;
import com.erp.erp.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/produccion")
public class ProduccionController {

    private final ProduccionService produccionService;
    private final EscandalloService escandalloService;
    private final ProductoService productoService;
    private final EmpleadoService empleadoService;

    public ProduccionController(ProduccionService produccionService,
                                EscandalloService escandalloService,
                                ProductoService productoService,
                                EmpleadoService empleadoService) {
        this.produccionService = produccionService;
        this.escandalloService = escandalloService;
        this.productoService = productoService;
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        model.addAttribute("fabricaciones", produccionService.listarFabricaciones());
        return "produccion/list";
    }

    @GetMapping("/nueva")
    public String nuevaFabricacion(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Fabricacion f = new Fabricacion();
        f.setFecha(LocalDate.now());
        model.addAttribute("fabricacion", f);
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("empleados", empleadoService.listar());
        return "produccion/form";
    }

    @PostMapping("/guardar")
    public String guardarFabricacion(@RequestParam Integer idProducto,
                                    @RequestParam Integer idEmpleado,
                                    @RequestParam Integer unidades,
                                    @RequestParam String fecha,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        if (unidades == null || unidades <= 0) {
            ra.addFlashAttribute("errorMessage", "Unidades deben ser mayor que 0.");
            return "redirect:/produccion/nueva";
        }
        Producto p = productoService.obtener(idProducto);
        if (p == null) {
            ra.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/produccion/nueva";
        }
        if (p.getEsCompuesto() != null && p.getEsCompuesto()) {
            List<Escandallo> esc = escandalloService.listarPorProductoCompuesto(p.getId());
            if (esc == null || esc.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Producto compuesto sin escandallo definido. Defina el escandallo primero.");
                return "redirect:/produccion/nueva";
            }
        }
        Fabricacion fab = new Fabricacion();
        fab.setProducto(p);
        fab.setEmpleado(empleadoService.obtener(idEmpleado));
        fab.setUnidades(unidades);
        fab.setFecha(LocalDate.parse(fecha));
        produccionService.ejecutarFabricacion(fab);
        ra.addFlashAttribute("mensaje", "Fabricación registrada correctamente.");
        return "redirect:/produccion";
    }

    @GetMapping("/escandallo/{idProducto}")
    public String escandallo(@PathVariable Integer idProducto, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Producto producto = productoService.obtener(idProducto);
        if (producto == null) return "redirect:/productos";
        model.addAttribute("producto", producto);
        model.addAttribute("lineas", escandalloService.listarPorProductoCompuesto(idProducto));
        model.addAttribute("productos", productoService.listar());
        return "produccion/escandallo";
    }

    @PostMapping("/escandallo/guardar")
    public String guardarEscandallo(@RequestParam Integer idProductoCompuesto,
                                   @RequestParam(required = false) List<String> idComponentes,
                                   @RequestParam(required = false) List<String> cantidades,
                                   @RequestParam(required = false) List<String> tiposComponente,
                                   @RequestParam(required = false) List<String> preciosCosto,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Producto compuesto = productoService.obtener(idProductoCompuesto);
        if (compuesto == null) {
            ra.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/productos";
        }
        compuesto.setEsCompuesto(true);
        productoService.guardar(compuesto);

        List<Escandallo> lineas = new ArrayList<>();
        if (idComponentes != null && cantidades != null) {
            for (int i = 0; i < idComponentes.size(); i++) {
                if (i >= cantidades.size()) break;
                Integer idComp = null;
                try {
                    String s = idComponentes.get(i);
                    if (s == null || s.trim().isEmpty()) continue;
                    idComp = Integer.parseInt(s);
                } catch (NumberFormatException ex) { continue; }
                Double cant = null;
                try {
                    String s = cantidades.get(i);
                    if (s == null || s.trim().isEmpty()) continue;
                    cant = Double.parseDouble(s);
                } catch (NumberFormatException ex) { continue; }
                if (idComp == null || cant == null || cant <= 0) continue;
                Escandallo e = new Escandallo();
                e.setProductoCompuesto(compuesto);
                e.setComponente(productoService.obtener(idComp));
                e.setCantidad(cant);
                e.setTipoComponente(tiposComponente != null && i < tiposComponente.size() && tiposComponente.get(i) != null ? tiposComponente.get(i) : "P");
                Double precio = null;
                if (preciosCosto != null && i < preciosCosto.size() && preciosCosto.get(i) != null && !preciosCosto.get(i).trim().isEmpty()) {
                    try { precio = Double.parseDouble(preciosCosto.get(i)); } catch (NumberFormatException ex) { }
                }
                e.setPrecioCostoUnidad(precio);
                if (e.getComponente() != null) lineas.add(e);
            }
        }
        escandalloService.guardarEscandallo(compuesto, lineas);
        ra.addFlashAttribute("mensaje", "Escandallo guardado.");
        ra.addAttribute("idProducto", idProductoCompuesto);
        return "redirect:/produccion/escandallo/{idProducto}";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarFabricacion(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        
        Fabricacion fabricacion = produccionService.obtenerFabricacion(id);
        if (fabricacion == null) {
            ra.addFlashAttribute("errorMessage", "Fabricación no encontrada.");
            return "redirect:/produccion";
        }
        
        produccionService.eliminarFabricacion(id);
        ra.addFlashAttribute("mensaje", "Fabricación eliminada correctamente. Stocks revertidos.");
        return "redirect:/produccion";
    }
}
