package com.erp.erp.controller;

import com.erp.erp.model.Cliente;
import com.erp.erp.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("clientes", service.listar());
        return "clientes/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", service.obtener(id));
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(Cliente cliente) {
        service.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable Integer id) {
        service.borrar(id);
        return "redirect:/clientes";
    }
}
