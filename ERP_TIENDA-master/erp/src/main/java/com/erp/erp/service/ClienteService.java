package com.erp.erp.service;

import com.erp.erp.model.Cliente;
import com.erp.erp.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public void guardar(Cliente cliente) {
        repo.save(cliente);
    }

    public Cliente obtener(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public void borrar(Integer id) {
        repo.deleteById(id);
    }
}
