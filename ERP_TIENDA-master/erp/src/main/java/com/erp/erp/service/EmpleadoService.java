package com.erp.erp.service;

import com.erp.erp.model.Empleado;
import com.erp.erp.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public List<Empleado> listar() {
        return repository.findAll();
    }

    public Empleado obtener(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void guardar(Empleado empleado) {
        repository.save(empleado);
    }

    public void borrar(Integer id) {
        repository.deleteById(id);
    }
}
