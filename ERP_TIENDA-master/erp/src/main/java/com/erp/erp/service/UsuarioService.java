package com.erp.erp.service;

import com.erp.erp.model.Usuario;
import com.erp.erp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.erp.erp.security.PasswordUtils;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario login(String email, String password) {

        String passwordHash = PasswordUtils.hashPassword(password);

        return repo.findByEmailAndPassword(email, passwordHash)
                .orElse(null);
    }

    public boolean existeEmail(String email) {
        return repo.findByEmail(email).isPresent();
    }

    public void guardar(Usuario usuario) {
        repo.save(usuario);
    }
}
