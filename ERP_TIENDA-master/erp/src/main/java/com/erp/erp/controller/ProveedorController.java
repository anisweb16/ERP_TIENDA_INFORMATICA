package com.erp.erp.controller;

import com.erp.erp.model.Proveedor;
import com.erp.erp.service.ProveedorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("proveedores", service.listar());
        return "proveedores/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("proveedor", service.obtener(id));
        return "proveedores/form";
    }

    @PostMapping("/guardar")
    public String guardar(Proveedor proveedor) {
        service.guardar(proveedor);
        return "redirect:/proveedores";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id) {
        service.borrar(id);
        return "redirect:/proveedores";
    }
}
