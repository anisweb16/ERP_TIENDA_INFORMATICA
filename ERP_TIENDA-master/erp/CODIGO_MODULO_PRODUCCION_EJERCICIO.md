# Código directo – Módulo de Producción (ejercicio ERP)

A continuación se listan todos los archivos creados o modificados para el módulo de producción, con su código completo.

---

## 1. MODELO (Java)

### `src/main/java/com/erp/erp/model/Producto.java`

```java
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
```

### `src/main/java/com/erp/erp/model/Escandallo.java`

```java
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

    @ManyToOne
    @JoinColumn(name = "id_producto_compuesto", nullable = false)
    private Producto productoCompuesto;

    @ManyToOne
    @JoinColumn(name = "id_componente", nullable = false)
    private Producto componente;

    @Column(nullable = false)
    private Double cantidad;

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
```

### `src/main/java/com/erp/erp/model/Fabricacion.java`

```java
package com.erp.erp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fabricacion")
public class Fabricacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fabricacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer unidades;

    @Column(nullable = false)
    private LocalDate fecha;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getUnidades() { return unidades; }
    public void setUnidades(Integer unidades) { this.unidades = unidades; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
```

---

## 2. REPOSITORIOS (Java)

### `src/main/java/com/erp/erp/repository/EscandalloRepository.java`

```java
package com.erp.erp.repository;

import com.erp.erp.model.Escandallo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscandalloRepository extends JpaRepository<Escandallo, Integer> {

    List<Escandallo> findByProductoCompuestoIdOrderById(Integer idProductoCompuesto);
    void deleteByProductoCompuesto_Id(Integer idProductoCompuesto);
}
```

### `src/main/java/com/erp/erp/repository/FabricacionRepository.java`

```java
package com.erp.erp.repository;

import com.erp.erp.model.Fabricacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FabricacionRepository extends JpaRepository<Fabricacion, Integer> {

    List<Fabricacion> findAllByOrderByFechaDesc();
}
```

---

## 3. SERVICIOS (Java)

### `src/main/java/com/erp/erp/service/EscandalloService.java`

```java
package com.erp.erp.service;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.EscandalloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EscandalloService {

    private final EscandalloRepository escandalloRepository;
    private final ProductoService productoService;

    public EscandalloService(EscandalloRepository escandalloRepository,
                             ProductoService productoService) {
        this.escandalloRepository = escandalloRepository;
        this.productoService = productoService;
    }

    public List<Escandallo> listarPorProductoCompuesto(Integer idProducto) {
        return escandalloRepository.findByProductoCompuestoIdOrderById(idProducto);
    }

    @Transactional
    public void guardarEscandallo(Producto productoCompuesto, List<Escandallo> lineas) {
        if (productoCompuesto == null || productoCompuesto.getId() == null) return;
        escandalloRepository.deleteByProductoCompuesto_Id(productoCompuesto.getId());
        if (lineas != null) {
            for (Escandallo e : lineas) {
                if (e.getComponente() == null || e.getComponente().getId() == null) continue;
                if (e.getCantidad() == null || e.getCantidad() <= 0) continue;
                e.setProductoCompuesto(productoCompuesto);
                e.setComponente(productoService.obtener(e.getComponente().getId()));
                if (e.getComponente() != null) {
                    escandalloRepository.save(e);
                }
            }
        }
    }

    @Transactional
    public void borrarPorProductoCompuesto(Integer idProducto) {
        escandalloRepository.deleteByProductoCompuesto_Id(idProducto);
    }
}
```

### `src/main/java/com/erp/erp/service/ProduccionService.java`

```java
package com.erp.erp.service;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Fabricacion;
import com.erp.erp.model.Producto;
import com.erp.erp.repository.EscandalloRepository;
import com.erp.erp.repository.FabricacionRepository;
import com.erp.erp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProduccionService {

    private final FabricacionRepository fabricacionRepository;
    private final EscandalloRepository escandalloRepository;
    private final ProductoRepository productoRepository;

    public ProduccionService(FabricacionRepository fabricacionRepository,
                             EscandalloRepository escandalloRepository,
                             ProductoRepository productoRepository) {
        this.fabricacionRepository = fabricacionRepository;
        this.escandalloRepository = escandalloRepository;
        this.productoRepository = productoRepository;
    }

    public List<Fabricacion> listarFabricaciones() {
        return fabricacionRepository.findAllByOrderByFechaDesc();
    }

    @Transactional
    public void ejecutarFabricacion(Fabricacion fabricacion) {
        if (fabricacion == null || fabricacion.getProducto() == null || fabricacion.getUnidades() == null || fabricacion.getUnidades() <= 0)
            return;

        Producto producto = productoRepository.findById(fabricacion.getProducto().getId()).orElse(null);
        if (producto == null) return;

        int unidades = fabricacion.getUnidades();

        if (Boolean.TRUE.equals(producto.getEsCompuesto())) {
            List<Escandallo> escandallo = escandalloRepository.findByProductoCompuestoIdOrderById(producto.getId());
            for (Escandallo e : escandallo) {
                if (e.getComponente() == null) continue;
                Producto comp = productoRepository.findById(e.getComponente().getId()).orElse(null);
                if (comp == null) continue;
                double necesario = (e.getCantidad() == null ? 0 : e.getCantidad()) * unidades;
                int restar = (int) Math.ceil(necesario);
                int stockActual = comp.getStock() == null ? 0 : comp.getStock();
                comp.setStock(Math.max(0, stockActual - restar));
                productoRepository.save(comp);
            }
        }

        int stockActual = producto.getStock() == null ? 0 : producto.getStock();
        producto.setStock(stockActual + unidades);
        productoRepository.save(producto);

        fabricacion.setProducto(producto);
        fabricacionRepository.save(fabricacion);
    }
}
```

---

## 4. CONTROLADOR (Java)

### `src/main/java/com/erp/erp/controller/ProduccionController.java`

```java
package com.erp.erp.controller;

import com.erp.erp.model.Escandallo;
import com.erp.erp.model.Fabricacion;
import com.erp.erp.model.Producto;
import com.erp.erp.service.EscandalloService;
import com.erp.erp.service.EmpleadoService;
import com.erp.erp.service.ProduccionService;
import com.erp.erp.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/produccion")
public class ProduccionController {

    private final ProduccionService produccionService;
    private final EscandalloService escandalloService;
    private final ProductoService productoService;
    private final EmpleadoService empleadoService;

    public ProduccionController(ProduccionService produccionService,
                                EscandalloService escandalloService,
                                ProductoService productoService,
                                EmpleadoService empleadoService) {
        this.produccionService = produccionService;
        this.escandalloService = escandalloService;
        this.productoService = productoService;
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        model.addAttribute("fabricaciones", produccionService.listarFabricaciones());
        return "produccion/list";
    }

    @GetMapping("/nueva")
    public String nuevaFabricacion(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Fabricacion f = new Fabricacion();
        f.setFecha(LocalDate.now());
        model.addAttribute("fabricacion", f);
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("empleados", empleadoService.listar());
        return "produccion/form";
    }

    @PostMapping("/guardar")
    public String guardarFabricacion(@RequestParam Integer idProducto,
                                    @RequestParam Integer idEmpleado,
                                    @RequestParam Integer unidades,
                                    @RequestParam String fecha,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        if (unidades == null || unidades <= 0) {
            ra.addFlashAttribute("errorMessage", "Unidades deben ser mayor que 0.");
            return "redirect:/produccion/nueva";
        }
        Producto p = productoService.obtener(idProducto);
        if (p == null) {
            ra.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/produccion/nueva";
        }
        if (p.getEsCompuesto() != null && p.getEsCompuesto()) {
            List<Escandallo> esc = escandalloService.listarPorProductoCompuesto(p.getId());
            if (esc == null || esc.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Producto compuesto sin escandallo definido. Defina el escandallo primero.");
                return "redirect:/produccion/nueva";
            }
        }
        Fabricacion fab = new Fabricacion();
        fab.setProducto(p);
        fab.setEmpleado(empleadoService.obtener(idEmpleado));
        fab.setUnidades(unidades);
        fab.setFecha(LocalDate.parse(fecha));
        produccionService.ejecutarFabricacion(fab);
        ra.addFlashAttribute("mensaje", "Fabricación registrada correctamente.");
        return "redirect:/produccion";
    }

    @GetMapping("/escandallo/{idProducto}")
    public String escandallo(@PathVariable Integer idProducto, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Producto producto = productoService.obtener(idProducto);
        if (producto == null) return "redirect:/productos";
        model.addAttribute("producto", producto);
        model.addAttribute("lineas", escandalloService.listarPorProductoCompuesto(idProducto));
        model.addAttribute("productos", productoService.listar());
        return "produccion/escandallo";
    }

    @PostMapping("/escandallo/guardar")
    public String guardarEscandallo(@RequestParam Integer idProductoCompuesto,
                                   @RequestParam(required = false) List<String> idComponentes,
                                   @RequestParam(required = false) List<String> cantidades,
                                   @RequestParam(required = false) List<String> tiposComponente,
                                   @RequestParam(required = false) List<String> preciosCosto,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Producto compuesto = productoService.obtener(idProductoCompuesto);
        if (compuesto == null) {
            ra.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/productos";
        }
        compuesto.setEsCompuesto(true);
        productoService.guardar(compuesto);

        List<Escandallo> lineas = new ArrayList<>();
        if (idComponentes != null && cantidades != null) {
            for (int i = 0; i < idComponentes.size(); i++) {
                if (i >= cantidades.size()) break;
                Integer idComp = null;
                try {
                    String s = idComponentes.get(i);
                    if (s == null || s.trim().isEmpty()) continue;
                    idComp = Integer.parseInt(s);
                } catch (NumberFormatException ex) { continue; }
                Double cant = null;
                try {
                    String s = cantidades.get(i);
                    if (s == null || s.trim().isEmpty()) continue;
                    cant = Double.parseDouble(s);
                } catch (NumberFormatException ex) { continue; }
                if (idComp == null || cant == null || cant <= 0) continue;
                Escandallo e = new Escandallo();
                e.setProductoCompuesto(compuesto);
                e.setComponente(productoService.obtener(idComp));
                e.setCantidad(cant);
                e.setTipoComponente(tiposComponente != null && i < tiposComponente.size() && tiposComponente.get(i) != null ? tiposComponente.get(i) : "P");
                Double precio = null;
                if (preciosCosto != null && i < preciosCosto.size() && preciosCosto.get(i) != null && !preciosCosto.get(i).trim().isEmpty()) {
                    try { precio = Double.parseDouble(preciosCosto.get(i)); } catch (NumberFormatException ex) { }
                }
                e.setPrecioCostoUnidad(precio);
                if (e.getComponente() != null) lineas.add(e);
            }
        }
        escandalloService.guardarEscandallo(compuesto, lineas);
        ra.addFlashAttribute("mensaje", "Escandallo guardado.");
        ra.addAttribute("idProducto", idProductoCompuesto);
        return "redirect:/produccion/escandallo/{idProducto}";
    }
}
```

---

## 5. BASE DE DATOS (SQL)

### `src/main/resources/schema-modulo-produccion.sql`

```sql
-- MÓDULO DE PRODUCCIÓN - Actualización del modelo lógico BBDD
-- Ejecutar en la base de datos erp_tienda

USE erp_tienda;

ALTER TABLE productos
  ADD COLUMN es_compuesto TINYINT(1) NOT NULL DEFAULT 0
  COMMENT '0=Simple, 1=Compuesto (tiene escandallo)';

CREATE TABLE IF NOT EXISTS escandallo (
  id_escandallo INT AUTO_INCREMENT PRIMARY KEY,
  id_producto_compuesto INT NOT NULL,
  id_componente INT NOT NULL,
  cantidad DECIMAL(12,4) NOT NULL,
  tipo_componente CHAR(1) NOT NULL DEFAULT 'P' COMMENT 'P=Producto, S=Servicio/Mano obra, R=Recurso',
  precio_costo_ud DECIMAL(12,2) NULL,
  CONSTRAINT fk_escandallo_compuesto FOREIGN KEY (id_producto_compuesto) REFERENCES productos(id) ON DELETE CASCADE,
  CONSTRAINT fk_escandallo_componente FOREIGN KEY (id_componente) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fabricacion (
  id_fabricacion INT AUTO_INCREMENT PRIMARY KEY,
  id_empleado INT NOT NULL,
  id_producto INT NOT NULL,
  unidades INT NOT NULL,
  fecha DATE NOT NULL,
  CONSTRAINT fk_fabricacion_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id),
  CONSTRAINT fk_fabricacion_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
);
```

---

## 6. VISTAS (Thymeleaf)

### `src/main/resources/templates/produccion/list.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="es">
<head th:replace="~{fragments/layout :: head('Producción | ERP Tienda Gaming')}"></head>
<body>
<div class="app">
  <aside th:replace="~{fragments/layout :: sidebar}"></aside>

  <main class="content">
    <div class="page-header">
      <div>
        <h1 class="page-title">Producción</h1>
        <p class="page-subtitle">Registro de fabricaciones y escandallos.</p>
      </div>
      <a class="btn primary" th:href="@{/produccion/nueva}"><i class="fa-solid fa-gears"></i> Nueva fabricación</a>
    </div>

    <p th:if="${mensaje}" class="helper" style="color:var(--ok);margin-bottom:12px" th:text="${mensaje}"></p>

    <section class="panel">
      <div class="table-wrap">
        <table class="data-table">
          <thead>
          <tr>
            <th>Fecha</th>
            <th>Producto</th>
            <th>Unidades</th>
            <th>Empleado</th>
          </tr>
          </thead>
          <tbody>
          <tr th:each="f : ${fabricaciones}">
            <td th:text="${f.fecha}">fecha</td>
            <td th:text="${f.producto != null ? f.producto.nombre : '-'}">producto</td>
            <td th:text="${f.unidades}">0</td>
            <td th:text="${f.empleado != null ? f.empleado.nombre : '-'}">empleado</td>
          </tr>
          <tr th:if="${#lists.isEmpty(fabricaciones)}">
            <td colspan="4" style="text-align:center;color:var(--muted)">No hay fabricaciones registradas.</td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>
</body>
</html>
```

### `src/main/resources/templates/produccion/form.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="es">
<head th:replace="~{fragments/layout :: head('Nueva fabricación | ERP Tienda Gaming')}"></head>
<body>
<div class="app">
  <aside th:replace="~{fragments/layout :: sidebar}"></aside>

  <main class="content">
    <div class="page-header">
      <div>
        <h1 class="page-title">Nueva fabricación</h1>
        <p class="page-subtitle">Producto, unidades, empleado y fecha.</p>
      </div>
      <a class="btn" th:href="@{/produccion}"><i class="fa-solid fa-arrow-left"></i> Volver</a>
    </div>

    <p th:if="${errorMessage}" class="helper" style="color:var(--danger);margin-bottom:12px" th:text="${errorMessage}"></p>

    <section class="panel">
      <form th:action="@{/produccion/guardar}" method="post">
        <div class="form-grid">
          <div class="field full">
            <label>Producto a fabricar</label>
            <select name="idProducto" required>
              <option value="" disabled selected>Selecciona producto…</option>
              <option th:each="p : ${productos}" th:value="${p.id}" th:text="${p.nombre}"></option>
            </select>
          </div>
          <div class="field">
            <label>Unidades</label>
            <input type="number" name="unidades" value="1" min="1" required />
          </div>
          <div class="field">
            <label>Fecha</label>
            <input type="date" name="fecha" th:value="${fabricacion != null and fabricacion.fecha != null} ? ${#temporals.format(fabricacion.fecha, 'yyyy-MM-dd')} : ${#temporals.format(#temporals.createNow(), 'yyyy-MM-dd')}" required />
          </div>
          <div class="field full">
            <label>Empleado</label>
            <select name="idEmpleado" required>
              <option value="" disabled selected>Selecciona empleado…</option>
              <option th:each="e : ${empleados}" th:value="${e.id}" th:text="${e.nombre}">empleado</option>
            </select>
          </div>
        </div>
        <div style="display:flex;gap:10px;justify-content:flex-end;margin-top:16px">
          <a class="btn ghost" th:href="@{/produccion}">Cancelar</a>
          <button class="btn primary" type="submit"><i class="fa-solid fa-gears"></i> Ejecutar fabricación</button>
        </div>
      </form>
    </section>
  </main>
</div>
</body>
</html>
```

### `src/main/resources/templates/produccion/escandallo.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="es">
<head th:replace="~{fragments/layout :: head('Escandallo | ERP Tienda Gaming')}"></head>
<body>
<div class="app">
  <aside th:replace="~{fragments/layout :: sidebar}"></aside>

  <main class="content">
    <div class="page-header">
      <div>
        <h1 class="page-title">Escandallo: <span th:text="${producto.nombre}">Producto</span></h1>
        <p class="page-subtitle">Lista de materiales (componentes) del producto compuesto.</p>
      </div>
      <a class="btn" th:href="@{/productos}"><i class="fa-solid fa-arrow-left"></i> Volver a productos</a>
    </div>

    <p th:if="${mensaje}" class="helper" style="color:var(--ok);margin-bottom:12px" th:text="${mensaje}"></p>

    <section class="panel">
      <form th:action="@{/produccion/escandallo/guardar}" method="post">
        <input type="hidden" name="idProductoCompuesto" th:value="${producto.id}" />

        <div class="invoice-table-box" style="margin-bottom:14px">
          <table class="invoice-lines">
            <thead>
            <tr>
              <th style="width:35%">Componente</th>
              <th style="width:15%">Cantidad</th>
              <th style="width:15%">Tipo (P/S/R)</th>
              <th style="width:15%">Coste/ud</th>
              <th style="width:10%">Acción</th>
            </tr>
            </thead>
            <tbody id="lineasEscandallo">
            <tr th:each="lin, stat : ${lineas}">
              <td>
                <select name="idComponentes" required>
                  <option value="" disabled>Selecciona…</option>
                  <option th:each="p : ${productos}" th:value="${p.id}" th:text="${p.nombre}" th:selected="${lin.componente != null and lin.componente.id == p.id}"></option>
                </select>
              </td>
              <td><input type="number" name="cantidades" step="0.0001" th:value="${lin.cantidad}" min="0.0001" required style="width:100%" /></td>
              <td>
                <select name="tiposComponente" style="width:100%">
                  <option value="P" th:selected="${lin.tipoComponente == 'P'}">P (Producto)</option>
                  <option value="S" th:selected="${lin.tipoComponente == 'S'}">S (Servicio)</option>
                  <option value="R" th:selected="${lin.tipoComponente == 'R'}">R (Recurso)</option>
                </select>
              </td>
              <td><input type="number" name="preciosCosto" step="0.01" th:value="${lin.precioCostoUnidad}" min="0" style="width:100%" /></td>
              <td style="text-align:center"><button type="button" class="btn danger" onclick="eliminarFila(this)">✕</button></td>
            </tr>
            <tr th:if="${#lists.isEmpty(lineas)}">
              <td>
                <select name="idComponentes" required>
                  <option value="" disabled selected>Selecciona componente…</option>
                  <option th:each="p : ${productos}" th:value="${p.id}" th:text="${p.nombre}"></option>
                </select>
              </td>
              <td><input type="number" name="cantidades" step="0.0001" value="1" min="0.0001" required style="width:100%" /></td>
              <td>
                <select name="tiposComponente" style="width:100%">
                  <option value="P" selected>P (Producto)</option>
                  <option value="S">S (Servicio)</option>
                  <option value="R">R (Recurso)</option>
                </select>
              </td>
              <td><input type="number" name="preciosCosto" step="0.01" value="0" min="0" style="width:100%" /></td>
              <td style="text-align:center"><button type="button" class="btn danger" onclick="eliminarFila(this)">✕</button></td>
            </tr>
            <tr id="filaTemplateEscandallo" th:if="${!#lists.isEmpty(lineas)}" style="display:none">
              <td>
                <select name="idComponentes">
                  <option value="" disabled selected>Selecciona…</option>
                  <option th:each="p : ${productos}" th:value="${p.id}" th:text="${p.nombre}"></option>
                </select>
              </td>
              <td><input type="number" name="cantidades" step="0.0001" value="1" min="0.0001" style="width:100%" /></td>
              <td>
                <select name="tiposComponente" style="width:100%">
                  <option value="P" selected>P</option>
                  <option value="S">S</option>
                  <option value="R">R</option>
                </select>
              </td>
              <td><input type="number" name="preciosCosto" step="0.01" value="0" min="0" style="width:100%" /></td>
              <td style="text-align:center"><button type="button" class="btn danger" onclick="eliminarFila(this)">✕</button></td>
            </tr>
            </tbody>
          </table>
        </div>
        <div style="margin-bottom:14px">
          <button type="button" class="btn ghost" onclick="agregarFila()"><i class="fa-solid fa-plus"></i> Añadir componente</button>
        </div>
        <div style="display:flex;gap:10px;justify-content:flex-end">
          <button class="btn primary" type="submit"><i class="fa-solid fa-floppy-disk"></i> Guardar escandallo</button>
        </div>
      </form>
    </section>
  </main>
</div>
<script>
  function agregarFila() {
    const tbody = document.getElementById('lineasEscandallo');
    const template = document.getElementById('filaTemplateEscandallo');
    const fila = template ? template : tbody.querySelector('tr');
    const nueva = fila.cloneNode(true);
    if (nueva.id) nueva.removeAttribute('id');
    nueva.style.display = '';
    nueva.querySelectorAll('input[type="number"]').forEach(i => { i.value = i.name === 'cantidades' ? '1' : '0'; });
    nueva.querySelectorAll('select').forEach(s => { if (s.name !== 'tiposComponente') s.selectedIndex = 0; });
    tbody.appendChild(nueva);
  }
  function eliminarFila(btn) {
    const tr = btn.closest('tr');
    if (tr.id === 'filaTemplateEscandallo') return;
    if (document.querySelectorAll('#lineasEscandallo tr:not(#filaTemplateEscandallo)').length > 1) tr.remove();
  }
</script>
</body>
</html>
```

---

## 7. MODIFICACIONES EN PRODUCTOS Y LAYOUT

### Fragmento a añadir en `productos/form.html` (dentro del form, antes del cierre de `</div>` del form-grid):

```html
          <div class="field full">
            <label>Tipo de producto</label>
            <select th:field="*{esCompuesto}">
              <option th:value="false">Simple</option>
              <option th:value="true">Compuesto (con escandallo)</option>
            </select>
            <p class="helper" th:if="${producto.id != null and producto.esCompuesto}">
              <a th:href="@{/produccion/escandallo/{id}(id=${producto.id})}" class="link"><i class="fa-solid fa-list"></i> Gestionar escandallo</a>
            </p>
          </div>
```

### En `productos/list.html`: columna Tipo y enlace Escandallo

- Añadir `<th>Tipo</th>` en la cabecera.
- En cada fila: `<td><span th:text="${(p.esCompuesto != null and p.esCompuesto) ? 'Compuesto' : 'Simple'}">Simple</span></td>`.
- En acciones: `<a th:if="${p.esCompuesto}" class="link" th:href="@{/produccion/escandallo/{id}(id=${p.id})}"><i class="fa-solid fa-list"></i> Escandallo</a>`.

### En `fragments/layout.html` (dentro de `<nav class="nav">`, antes de Cerrar sesión):

```html
        <a th:href="@{/produccion}">
            <i class="fa-solid fa-gears"></i> Producción
        </a>
```

---

*Para el contenido completo de `produccion/escandallo.html` utiliza el archivo existente en el proyecto; el listado anterior indica su estructura y enlaces.*
