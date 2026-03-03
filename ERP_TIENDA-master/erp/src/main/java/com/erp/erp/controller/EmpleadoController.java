package com.erp.erp.controller;

import com.erp.erp.model.Empleado;
import com.erp.erp.service.EmpleadoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        model.addAttribute("empleados", service.listar());
        return "empleados/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        model.addAttribute("empleado", new Empleado());
        return "empleados/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        model.addAttribute("empleado", service.obtener(id));
        return "empleados/form";
    }

    @PostMapping("/guardar")
    public String guardar(Empleado empleado) {
        service.guardar(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id) {
        service.borrar(id);
        return "redirect:/empleados";
    }
}
