# Ejemplo Práctico: Tienda Informática
## Guía de prueba del módulo de producción

---

## 🎯 Contexto: Montaje de Ordenadores

Ejemplo simple para una tienda de informática:
- **Producto terminado**: Ordenador PC
- **Componente**: Torre PC
- **Materias primas**: Componentes básicos

---

## 📋 PASO 1: Crear MATERIAS PRIMAS (productos simples)

Ir a **Productos** → **Nuevo producto** y crear:

| Nombre | Precio | Stock | Tipo |
|--------|--------|-------|------|
| Placa base | 80€ | 10 | Simple |
| Procesador | 120€ | 10 | Simple |
| Memoria RAM 8GB | 40€ | 15 | Simple |
| Disco SSD 256GB | 35€ | 15 | Simple |

---

## 📋 PASO 2: Crear un COMPONENTE (Torre PC)

### 2.1 Crear el producto
- **Nombre**: Torre PC
- **Precio**: 280€
- **Stock**: 0
- **Tipo**: **Compuesto (con escandallo)**

### 2.2 Definir el escandallo
Hacer clic en **"Gestionar escandallo"** y añadir:

| Componente | Cantidad | Tipo |
|------------|----------|------|
| Placa base | 1 | P |
| Procesador | 1 | P |
| Memoria RAM 8GB | 1 | P |
| Disco SSD 256GB | 1 | P |

---

## 📋 PASO 3: Crear PRODUCTO TERMINADO (Ordenador PC)

### 3.1 Crear el producto
- **Nombre**: Ordenador PC
- **Precio**: 450€
- **Stock**: 0
- **Tipo**: **Compuesto (con escandallo)**

### 3.2 Definir el escandallo
Hacer clic en **"Gestionar escandallo"** y añadir:

| Componente | Cantidad | Tipo |
|------------|----------|------|
| Torre PC | 1 | P |

---

## 📋 PASO 4: FABRICAR el COMPONENTE (Torre PC)

Ir a **Producción** → **Nueva fabricación**

```
Producto: Torre PC
Unidades: 5
Fecha: 2024-01-15
Empleado: [Seleccionar empleado]
```

Hacer clic en **"Ejecutar fabricación"**

### Resultado esperado:
| Producto | Stock ANTES | Stock DESPUÉS |
|----------|-------------|---------------|
| Placa base | 10 | 10 - (1×5) = **5** |
| Procesador | 10 | 10 - (1×5) = **5** |
| Memoria RAM 8GB | 15 | 15 - (1×5) = **10** |
| Disco SSD 256GB | 15 | 15 - (1×5) = **10** |
| **Torre PC** | 0 | 0 + 5 = **5** ✓ |

---

## 📋 PASO 5: FABRICAR el PRODUCTO TERMINADO

Ir a **Producción** → **Nueva fabricación**

```
Producto: Ordenador PC
Unidades: 3
Fecha: 2024-01-15
Empleado: [Seleccionar empleado]
```

Hacer clic en **"Ejecutar fabricación"**

### Resultado esperado:
| Producto | Stock ANTES | Stock DESPUÉS |
|----------|-------------|---------------|
| Torre PC | 5 | 5 - (1×3) = **2** |
| **Ordenador PC** | 0 | 0 + 3 = **3** ✓ |

---

## 📋 PASO 6: Verificar registros

Ir a **Producción** para ver:

| Fecha | Producto | Unidades | Empleado | Acciones |
|-------|----------|----------|----------|----------|
| 2024-01-15 | Torre PC | 5 | Juan | [Eliminar] |
| 2024-01-15 | Ordenador PC | 3 | Juan | [Eliminar] |

## 📋 PASO 7: ELIMINAR una fabricación

Si quieres deshacer una fabricación:

1. Ir a **Producción**
2. Hacer clic en **"Eliminar"** en la fila de la fabricación
3. Confirmar el mensaje: "¿Eliminar esta fabricación? Los stocks serán revertidos."

**El sistema revertirá:**
- Stock del producto fabricado (disminuye)
- Stock de los componentes (aumenta - se devuelven)

### Ejemplo: Eliminar fabricación de 5 Torres PC

| Producto | Stock ANTES de eliminar | Stock DESPUÉS |
|----------|-------------------------|---------------|
| Torre PC | 5 | 5 - 5 = **0** |
| Placa base | 5 | 5 + (1×5) = **10** |
| Procesador | 5 | 5 + (1×5) = **10** |
| Memoria RAM 8GB | 10 | 10 + (1×5) = **15** |
| Disco SSD 256GB | 10 | 10 + (1×5) = **15** |

---

## ✅ Resumen

| Acción | Componentes consumidos | Producto creado |
|--------|------------------------|-----------------|
| Fabricar 5 Torres | -5 placas, -5 procesadores, etc. | +5 Torres PC |
| Fabricar 3 PCs | -3 Torres PC | +3 Ordenadores |

**El sistema gestiona:**
- ✅ Stock de materias primas (disminuye)
- ✅ Stock de componentes (disminuye)
- ✅ Stock de productos terminados (aumenta)
- ✅ Fecha y empleado registrados

---

## 🚀 Para probar ahora

1. Iniciar aplicación: `cd ERP_TIENDA-master/erp && ./mvnw spring-boot:run`
2. Abrir http://localhost:8080
3. Seguir pasos 1 a 6
4. Verificar que los stocks evolucionan correctamente
