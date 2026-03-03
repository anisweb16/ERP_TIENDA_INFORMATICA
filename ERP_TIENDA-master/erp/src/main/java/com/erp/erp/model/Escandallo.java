package com.erp.erp.model;

import jakarta.persistence.*;

/**
 * Línea de escandallo (lista de materiales) de un producto compuesto.
 * tipoComponente: P = Producto/Parte, S = Servicio/Mano de obra, R = Recurso.
 */
@Entity
@Table(name = "escandallo")
public class Escandallo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escandallo")
    private Integer id;

    /** Producto compuesto al que pertenece este componente */
    @ManyToOne
    @JoinColumn(name = "id_producto_compuesto", nullable = false)
    private Producto productoCompuesto;

    /** Producto que actúa como componente (puede ser el mismo u otro) */
    @ManyToOne
    @JoinColumn(name = "id_componente", nullable = false)
    private Producto componente;

    /** Cantidad necesaria por unidad de producto compuesto */
    @Column(nullable = false)
    private Double cantidad;

    /** P = Producto/Parte, S = Servicio (mano de obra), R = Recurso */
    @Column(name = "tipo_componente", length = 1, nullable = false)
    private String tipoComponente = "P";

    @Column(name = "precio_costo_ud")
    private Double precioCostoUnidad;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Producto getProductoCompuesto() { return productoCompuesto; }
    public void setProductoCompuesto(Producto productoCompuesto) { this.productoCompuesto = productoCompuesto; }

    public Producto getComponente() { return componente; }
    public void setComponente(Producto componente) { this.componente = componente; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }

    public String getTipoComponente() { return tipoComponente; }
    public void setTipoComponente(String tipoComponente) { this.tipoComponente = tipoComponente; }

    public Double getPrecioCostoUnidad() { return precioCostoUnidad; }
    public void setPrecioCostoUnidad(Double precioCostoUnidad) { this.precioCostoUnidad = precioCostoUnidad; }
}
