package com.erp.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private Double precio;
    private Integer stock;

    /** true = producto compuesto (tiene escandallo), false = simple */
    @Column(name = "es_compuesto", nullable = false)
    private Boolean esCompuesto = false;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getEsCompuesto() { return esCompuesto != null ? esCompuesto : false; }
    public void setEsCompuesto(Boolean esCompuesto) { this.esCompuesto = esCompuesto != null ? esCompuesto : false; }
}
