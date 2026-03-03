package com.erp.erp.controller;

import com.erp.erp.security.PasswordUtils;
import com.erp.erp.model.Usuario;
import com.erp.erp.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UsuarioService service;

    public LoginController(UsuarioService service) {
        this.service = service;
    }

    // ===== LOGIN =====
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Usuario usuario = service.login(email, password);

        if (usuario == null) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }

        session.setAttribute("usuario", usuario);
        return "redirect:/dashboard";
    }

    // ===== REGISTER =====
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String procesarRegister(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        if (service.existeEmail(email)) {
            model.addAttribute("error", "El email ya existe");
            return "register";
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(PasswordUtils.hashPassword(password));
        service.guardar(u);

        model.addAttribute("mensaje", "Usuario registrado correctamente");
        return "login";
    }

    // ===== LOGOUT =====
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
