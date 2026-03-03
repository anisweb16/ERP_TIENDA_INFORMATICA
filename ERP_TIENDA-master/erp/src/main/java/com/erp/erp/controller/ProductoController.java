package com.erp.erp.controller;

import com.erp.erp.model.Producto;
import com.erp.erp.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("productos", service.listar());
        return "productos/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("producto", new Producto());
        return "productos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("producto", service.obtener(id));
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(Producto producto) {
        service.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id) {
        service.borrar(id);
        return "redirect:/productos";
    }
}
