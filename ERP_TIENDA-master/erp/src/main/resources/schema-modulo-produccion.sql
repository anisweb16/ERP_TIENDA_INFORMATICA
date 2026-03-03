-- ============================================================
-- MÓDULO DE PRODUCCIÓN - Actualización del modelo lógico BBDD
-- Ejecutar en la base de datos erp_tienda
-- ============================================================

-- 1) Añadir en productos: Simple / Compuesto
ALTER TABLE productos
  ADD COLUMN es_compuesto TINYINT(1) NOT NULL DEFAULT 0
  COMMENT '0=Simple, 1=Compuesto (tiene escandallo)';

-- 2) Tabla de escandallos (lista de materiales de productos compuestos)
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

-- 3) Tabla de productos fabricados (registro de fabricación)
CREATE TABLE IF NOT EXISTS fabricacion (
  id_fabricacion INT AUTO_INCREMENT PRIMARY KEY,
  id_empleado INT NOT NULL,
  id_producto INT NOT NULL,
  unidades INT NOT NULL,
  fecha DATE NOT NULL,
  CONSTRAINT fk_fabricacion_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id),
  CONSTRAINT fk_fabricacion_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
);
