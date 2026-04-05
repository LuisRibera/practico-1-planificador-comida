# Fase 3 - Pantalla de Recetas con Búsqueda y Filtro

## Resumen de cambios

Esta fase implementa la **pantalla de Recetas** completamente funcional con:
- ✅ Lista de recetas
- ✅ Búsqueda reactiva por nombre
- ✅ Filtro reactivo por ingrediente
- ✅ Mostrar ingredientes con cantidad y unidad
- ✅ Botón para eliminar receta
- ✅ FAB (botón flotante) para ir a Crear receta
- ✅ Todo en MVVM, sin lógica de negocio en UI

---

## Cambios realizados

### 1. **Actualización de `EstadoPlanificador`**
Se agregaron dos campos para almacenar estado de búsqueda/filtro:

```kotlin
val textoBusqueda: String = ""
val filtroIngrediente: String = ""
```

**Responsabilidad:** guardar el estado reactivo de filtros para mantener sincronía entre UI y datos.

---

### 2. **Actualización de `PlanificadorViewModel`**
Se agregaron 3 funciones públicas para manejar búsqueda/filtro:

- `actualizarTextoBusqueda(texto: String)`: actualiza el campo de búsqueda en el estado
- `actualizarFiltroIngrediente(ingrediente: String)`: actualiza el filtro de ingrediente en el estado
- `obtenerRecetasFiltradas(): List<Receta>`: devuelve recetas filtradas según búsqueda e ingrediente

**Responsabilidad:** toda la lógica de filtrado está en el ViewModel, no en la UI. La pantalla solo observa estado y llama a funciones.

---

### 3. **Componente `BarraBusqueda`** (reutilizable)
Archivo: `ui/componentes/BarraBusqueda.kt`

```kotlin
@Composable
fun BarraBusqueda(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String = "Buscar...",
    modifier: Modifier = Modifier
)
```

**Responsabilidad:** campo de texto genérico reutilizable para búsqueda y filtros.

---

### 4. **Componente `TarjetaReceta`** (reutilizable)
Archivo: `ui/componentes/TarjetaReceta.kt`

```kotlin
@Composable
fun TarjetaReceta(
    receta: Receta,
    alEliminar: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Responsabilidad:** mostrar una receta con sus ingredientes y botón eliminar. Es una presentación pura sin lógica.

---

### 5. **Pantalla `PantallaRecetas`** (implementación completa)
Archivo: `ui/pantallas/recetas/PantallaRecetas.kt`

**Flujo:**
1. Observa `estado: StateFlow` del ViewModel
2. Obtiene recetas filtradas del ViewModel (lógica pura)
3. Muestra dos barras de búsqueda (nombre e ingrediente)
4. Renderiza `LazyColumn` con tarjetas de recetas
5. Cada tarjeta tiene botón eliminar y botón FAB para crear

**Responsabilidad:** solo presentación. No contiene lógica de negocio, solo llama funciones del ViewModel.

---

### 6. **Actualización `PlanificadorApp` y `AppNavHost`**
Se instancia el ViewModel y se pasa a través de toda la estructura:

```
MainActivity 
  → PlanificadorApp (crea viewModel)
    → AppNavHost (recibe viewModel)
      → PantallaRecetas (recibe viewModel + navController)
```

**Responsabilidad:** inyectar dependencias de forma simple y manual (sin Hilt).

---

## Flujo reactivo (MVVM)

```
Usuario escribe en barra → 
  PantallaRecetas llama viewModel.actualizarTextoBusqueda(texto) → 
  ViewModel actualiza estado → 
  Estado se propaga vía StateFlow → 
  PantallaRecetas recompone → 
  Muestra recetas filtradas
```

Todo reactivo, sin callbacks complejos.

---

## Estructura de archivos (fase 3)

```
ui/
├── componentes/
│   ├── BarraBusqueda.kt          ← nuevo
│   ├── BarraNavegacionInferior.kt
│   └── TarjetaReceta.kt          ← nuevo
├── navegacion/
│   ├── AppNavHost.kt             ← modificado (recibe viewModel)
│   └── DestinoNavegacion.kt
├── pantallas/
│   ├── recetas/
│   │   └── PantallaRecetas.kt    ← implementación completa
│   ├── crear/
│   │   └── PantallaCrear.kt
│   ├── plansemanal/
│   │   └── PantallaPlanSemanal.kt
│   └── compras/
│       └── PantallaCompras.kt
├── PlanificadorApp.kt            ← modificado (instancia viewModel)
└── theme/
    └── ...

viewmodel/
├── PlanificadorViewModel.kt      ← modificado (3 funciones nuevas)
└── EstadoPlanificador.kt         ← modificado (2 campos nuevos)
```

---

## Qué observa la pantalla

- `estado.value.textoBusqueda` → barra de búsqueda por nombre
- `estado.value.filtroIngrediente` → barra de filtro por ingrediente
- `viewModel.obtenerRecetasFiltradas()` → lista renderizada

Cuando cambia cualquiera de estos, Compose recompone automáticamente la pantalla.

---

## Próximos pasos (opcionales)

- **Fase 4:** Pantalla Crear (formulario para agregar receta)
- **Fase 5:** Pantalla Plan Semanal (asignar recetas a días)
- **Fase 6:** Pantalla Compras (mostrar lista consolidada)
- **Polish:** Agregar iconos, animaciones, más estilos

---

## Notas de diseño

✅ **MVVM limpio:** UI separada de lógica  
✅ **Modular:** composables reutilizables  
✅ **Reactivo:** cambios en UI reflejan cambios en estado  
✅ **Simple:** sin arquitectura compleja para un estudiante  
✅ **Entendible:** código claro, sin trucos avanzados  

