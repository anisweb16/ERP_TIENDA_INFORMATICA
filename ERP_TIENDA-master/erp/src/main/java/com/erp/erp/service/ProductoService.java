package com.erp.erp.service;

import com.erp.erp.model.Producto;
import com.erp.erp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto obtener(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void guardar(Producto producto) {
        repository.save(producto);
    }

    public void borrar(Integer id) {
        repository.deleteById(id);
    }
}
