# Fase 1 - Planificador de comidas (MVVM en memoria)

Esta fase implementa solo modelos y `ViewModel`.
No incluye UI funcional todavia.

## Que incluye

- Modelo `Ingrediente` (`nombre`, `cantidad`)
- Modelo `Receta` (`id`, `nombre`, `ingredientes`)
- Modelo `ItemCompra` para compras consolidadas
- Estado `EstadoPlanificador` con:
  - `recetas`
  - `planSemanal` como `List<Receta?>` de tamano 7
  - `comprasConsolidadas`
- `PlanificadorViewModel` con `StateFlow`
- Una receta precargada por defecto
- Tests unitarios basicos del `ViewModel`

## Operaciones del ViewModel

- `agregarReceta(receta)`
- `eliminarReceta(idReceta)`
- `asignarRecetaADia(indiceDia, idReceta)`
- `limpiarDia(indiceDia)`
- `obtenerListaComprasConsolidada()`

Si se elimina una receta, tambien se limpia de cualquier dia del plan semanal.

La consolidacion de compras agrupa ingredientes por `nombre.trim().lowercase()`.

## Probar rapido

```powershell
./gradlew.bat :app:testDebugUnitTest
```

