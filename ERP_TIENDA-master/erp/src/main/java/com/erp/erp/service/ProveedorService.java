package com.erp.erp.service;

import com.erp.erp.model.Proveedor;
import com.erp.erp.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public List<Proveedor> listar() {
        return repository.findAll();
    }

    public Proveedor obtener(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void guardar(Proveedor proveedor) {
        repository.save(proveedor);
    }

    public void borrar(Integer id) {
        repository.deleteById(id);
    }
}
