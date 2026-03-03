# Actualización del ERP – Módulo de Producción

## 1. Modelo E/R (actualizado)

Se incorpora el **módulo de producción** al esquema de áreas funcionales:

```
MÓDULO COMPRAS    MÓDULO VENTAS
       \                /
        MÓDULO PRODUCCIÓN
       /       |        \
PERSONAL   PRODUCTOS   RECURSOS
       \       |       /
     MÓDULO STOCK
```

### Entidades nuevas o modificadas

- **productos** (modificada): se añade el atributo **es_compuesto** (Simple / Compuesto).
- **escandallo** (nueva): lista de materiales de un producto compuesto (componentes, cantidades, tipo P/S/R, coste/ud).
- **fabricacion** (nueva): registro de cada fabricación (empleado, producto fabricado, unidades, fecha).

### Relaciones

- Un **producto** compuesto tiene muchas líneas de **escandallo** (componentes).
- Cada línea de **escandallo** referencia al **producto compuesto** y al **producto componente**.
- Cada **fabricacion** referencia a un **empleado** y a un **producto** fabricado.

---

## 2. Modelo lógico de la BBDD

### 2.1 Tabla `productos` (modificación)

| Atributo     | Tipo         | Clave | Descripción                          |
|-------------|--------------|-------|--------------------------------------|
| id          | INT          | PK    | Identificador                        |
| nombre      | VARCHAR      |       | Nombre del producto                  |
| precio      | DECIMAL      |       | Precio de venta                      |
| stock       | INT          |       | Unidades en stock                    |
| **es_compuesto** | TINYINT(1) |       | 0 = Simple, 1 = Compuesto (escandallo) |

### 2.2 Tabla `escandallo`

| Atributo              | Tipo        | Clave | Descripción                                      |
|-----------------------|-------------|-------|--------------------------------------------------|
| id_escandallo         | INT         | PK    | Identificador de la línea                        |
| id_producto_compuesto | INT         | FK    | Producto al que pertenece el escandallo          |
| id_componente         | INT         | FK    | Producto que actúa como componente               |
| cantidad              | DECIMAL(12,4) |     | Cantidad necesaria por unidad de producto final  |
| tipo_componente       | CHAR(1)     |       | P = Producto, S = Servicio (mano de obra), R = Recurso |
| precio_costo_ud       | DECIMAL(12,2) |     | Coste por unidad del componente (opcional)       |

- FK `id_producto_compuesto` → `productos(id)` (ON DELETE CASCADE)
- FK `id_componente` → `productos(id)` (ON DELETE CASCADE)

### 2.3 Tabla `fabricacion`

| Atributo      | Tipo | Clave | Descripción                |
|---------------|------|-------|----------------------------|
| id_fabricacion| INT  | PK    | Identificador              |
| id_empleado   | INT  | FK    | Empleado que fabrica       |
| id_producto   | INT  | FK    | Producto fabricado         |
| unidades      | INT  |       | Unidades fabricadas        |
| fecha         | DATE |       | Fecha de la fabricación    |

- FK `id_empleado` → `empleados(id)`
- FK `id_producto` → `productos(id)`

El script SQL para aplicar estos cambios está en:  
`src/main/resources/schema-modulo-produccion.sql`

---

## 3. Proceso de producción (lógica)

1. **Definir el escandallo** de los productos compuestos (componentes, cantidades, tipo P/S/R, coste/ud).
2. **Al ejecutar una fabricación** de un producto compuesto:
   - Se restan del stock de cada **componente** las cantidades consumidas (cantidad del escandallo × unidades fabricadas).
   - Se suma al stock del **producto compuesto** las unidades fabricadas.
   - Se guarda el registro en **fabricacion** (fecha y empleado).
3. Para productos **simples**, la fabricación solo incrementa el stock del producto.

---

## 4. Interfaces del módulo de producción

### 4.1 Productos

- En el **formulario de producto** (nuevo/editar):
  - Campo **Tipo de producto**: *Simple* o *Compuesto (con escandallo)*.
  - Si el producto es compuesto y ya existe: enlace **“Gestionar escandallo”** a la pantalla de escandallo de ese producto.
- En la **lista de productos**:
  - Columna **Tipo**: Simple / Compuesto.
  - Para productos compuestos: enlace **“Escandallo”** a la gestión del escandallo.

### 4.2 Gestión de escandallos

- Ruta: **Producción → Escandallo** (desde producto compuesto) o **Productos → Escandallo** en la fila del producto.
- Pantalla: **Escandallo de [nombre del producto]**.
- Tabla de líneas: Componente (select de productos), Cantidad, Tipo (P/S/R), Coste/ud, Acción (borrar).
- Botón **“Añadir componente”** para nuevas líneas.
- Botón **“Guardar escandallo”** para persistir todas las líneas del producto.

### 4.3 Gestión de producción (fabricación)

- Menú: **Producción** (sidebar).
- **Listado de fabricaciones**: fecha, producto, unidades, empleado.
- **Nueva fabricación**: formulario con:
  - Producto a fabricar (select)
  - Unidades
  - Fecha
  - Empleado
- Botón **“Ejecutar fabricación”**: aplica la lógica de consumo de componentes y aumento de stock del producto y registra la fabricación.

---

## 5. Resumen de archivos del proyecto (módulo producción)

| Elemento | Ubicación |
|----------|-----------|
| Entidad Producto (esCompuesto) | `model/Producto.java` |
| Entidad Escandallo | `model/Escandallo.java` |
| Entidad Fabricacion | `model/Fabricacion.java` |
| Repositorios | `repository/EscandalloRepository.java`, `repository/FabricacionRepository.java` |
| Servicios | `service/EscandalloService.java`, `service/ProduccionService.java` |
| Controlador | `controller/ProduccionController.java` |
| SQL BBDD | `resources/schema-modulo-produccion.sql` |
| Vistas | `templates/produccion/list.html`, `form.html`, `escandallo.html` |
| Menú | `templates/fragments/layout.html` (enlace Producción) |
| Productos | `templates/productos/form.html` (tipo Simple/Compuesto, enlace escandallo), `productos/list.html` (columna Tipo, enlace Escandallo) |
