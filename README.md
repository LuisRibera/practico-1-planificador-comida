# Planificador de Comidas

Aplicacion Android para organizar recetas, planificar comidas semanales y generar automaticamente una lista de compras consolidada. Desarrollada con Jetpack Compose, arquitectura MVVM y navegacion basada en una barra inferior de cuatro pestanas.

---

## Indice

1. [Descripcion de pantallas](#descripcion-de-pantallas)
2. [Stack tecnologico](#stack-tecnologico)
3. [Arquitectura MVVM](#arquitectura-mvvm)
4. [Estructura de carpetas](#estructura-de-carpetas)
5. [Modelos de datos](#modelos-de-datos)
6. [API del ViewModel](#api-del-viewmodel)
7. [Flujo de datos](#flujo-de-datos)
8. [Navegacion](#navegacion)
9. [Como correr el proyecto](#como-correr-el-proyecto)
10. [Como correr los tests](#como-correr-los-tests)
11. [Roadmap](#roadmap)

---

## Descripcion de pantallas

### Recetas (`recetas`)

Pantalla principal de la aplicacion. Muestra la lista completa de recetas disponibles, cada una renderizada en una `TarjetaReceta` con nombre, ingredientes y boton de eliminacion.

- Dos campos de busqueda/filtrado en la parte superior:
  - **Buscar por nombre**: filtra recetas cuyo nombre contenga el texto ingresado (sin distincion de mayusculas).
  - **Filtrar por ingrediente**: filtra recetas que incluyan un ingrediente especifico.
- Ambos filtros se aplican de forma combinada en tiempo real.
- Un boton flotante (`FloatingActionButton`) con icono `+` en la esquina inferior derecha navega directamente a la pantalla Crear.
- Si no hay recetas que coincidan con los filtros activos, se muestra el mensaje "No hay recetas que coincidan con tu busqueda".
- Al inicio, la aplicacion precarga una receta de ejemplo ("Ensalada simple") para que la pantalla no se muestre vacia en el primer uso.

```
+----------------------------------+
| [Buscar por nombre...          ] |
| [Filtrar por ingrediente...    ] |
|                                  |
|  Ensalada simple                 |
|  - Lechuga 1.0 hoja              |
|  - Tomate 2.0 unidades           |
|  - Aceite de oliva 2.0 cdas      |
|  [Eliminar]                      |
|                                  |
|                          [ + ]   |
+----------------------------------+
```

---

### Crear receta (`crear`)

Formulario de alta de recetas. El estado del formulario (nombre y lista de ingredientes) es local a la pantalla usando `remember`, separado del estado global del ViewModel.

- Campo de texto para el nombre de la receta.
- Lista dinamica de filas de ingrediente (`FilaIngredienteEditable`), cada una con tres campos: nombre, cantidad (numerico) y unidad.
- Boton "Agregar ingrediente" que inserta una nueva fila vacia.
- Boton "Eliminar" por fila (se deshabilita cuando solo queda una fila).
- Validacion en el momento de intentar guardar (`mostrarErrores = true`):
  - El nombre es obligatorio.
  - Debe existir al menos un ingrediente valido (nombre no vacio, cantidad mayor a cero, unidad no vacia).
- Al guardar exitosamente se delega al ViewModel y se vuelve atras con `navController.popBackStack()`.
- El boton "Cancelar" descarta el formulario y vuelve atras sin modificar el estado.

```
+----------------------------------+
| Crear receta                     |
|                                  |
| Nombre de la receta: [_________] |
|                                  |
| Ingredientes:                    |
| [Nombre] [Cantidad] [Unidad] [X] |
| [Nombre] [Cantidad] [Unidad] [X] |
|                                  |
| [+ Agregar ingrediente]          |
|                                  |
| [Cancelar]          [Guardar]    |
+----------------------------------+
```

---

### Plan semanal (`plan_semanal`)

Vista semanal donde se asigna una receta a cada dia de la semana (lunes a domingo). Los nombres de los dias se cargan desde el recurso de cadenas `R.array.nombres_dias`.

- Siete filas en `LazyColumn`, una por dia, implementadas como `Card` elevados.
- Cada fila muestra el nombre del dia y la receta asignada (o "Sin receta asignada").
- Dos botones de accion por fila:
  - **Seleccionar receta**: abre un `AlertDialog` con la lista de recetas disponibles.
  - **Quitar**: elimina la asignacion del dia; deshabilitado si el dia no tiene receta.
- Al seleccionar una receta desde el dialogo, la lista de compras se recalcula automaticamente.

```
+----------------------------------+
| Plan semanal                     |
|                                  |
|  Lunes                           |
|  Ensalada simple                 |
|  [Seleccionar receta] [Quitar]   |
|                                  |
|  Martes                          |
|  Sin receta asignada             |
|  [Seleccionar receta] [Quitar]   |
|  ...                             |
+----------------------------------+
```

---

### Lista de compras (`compras`)

Lista generada automaticamente a partir de las recetas asignadas en el plan semanal. Los ingredientes de todas las recetas activas se consolidan sumando cantidades cuando el nombre (normalizado a minusculas sin espacios extremos) y la unidad coinciden.

- Los items se muestran ordenados alfabeticamente.
- Cada fila (`ItemCompraFila`) incluye un `Checkbox` para marcar el item como comprado.
- El estado "comprado" de cada item se mantiene en `itemsComprados` (un `Set<String>`) dentro del estado global.
- Si el plan semanal esta vacio, muestra el mensaje "No hay ingredientes en la lista de compras".
- Al eliminar o cambiar recetas del plan, los items comprados que ya no corresponden a ingredientes vigentes se eliminan automaticamente del set.

```
+----------------------------------+
| Lista de compras                 |
|                                  |
|  [x] Aceite de oliva  2.0 cdas   |
|  [ ] Lechuga          1.0 hoja   |
|  [ ] Tomate           2.0 uds    |
+----------------------------------+
```

---

## Stack tecnologico

| Componente                  | Tecnologia                        | Version       |
|-----------------------------|-----------------------------------|---------------|
| Lenguaje                    | Kotlin                            | 2.2.10        |
| UI Toolkit                  | Jetpack Compose                   | BOM 2024.09   |
| Componentes visuales        | Material3                         | (via BOM)     |
| Navegacion                  | Navigation Compose                | 2.8.2         |
| Gestion de estado           | ViewModel + StateFlow (Kotlin)    | 2.10.0        |
| Ciclo de vida               | Lifecycle Runtime KTX             | 2.10.0        |
| Activity base               | Activity Compose                  | 1.13.0        |
| Core Android                | Core KTX                          | 1.18.0        |
| Tests unitarios             | JUnit 4                           | 4.13.2        |
| Tests instrumentados        | AndroidX JUnit / Espresso         | 1.3.0 / 3.7.0 |
| Plugin de build             | Android Gradle Plugin (AGP)       | 9.1.0         |
| SDK minimo                  | Android 7.0 (API 24)              |               |
| SDK objetivo                | Android 16 (API 36)               |               |
| Java compatibility          | Java 11                           |               |

---

## Arquitectura MVVM

El proyecto implementa el patron **Model-View-ViewModel (MVVM)** recomendado por Android Jetpack. La comunicacion entre capas es unidireccional: la UI observa el estado del ViewModel y le envia eventos; el ViewModel actualiza el modelo y emite un nuevo estado inmutable.

```
+-----------------------------------------------------+
|                      UI LAYER                       |
|                                                     |
|  PantallaRecetas   PantallaCrear   PantallaPlan     |
|  PantallaCompras   Componentes reutilizables        |
|                                                     |
|  Observa: estado: StateFlow<EstadoPlanificador>     |
|  Invoca:  metodos publicos del ViewModel            |
+-------------------------+---------------------------+
                          |  eventos / llamadas
                          v
+-----------------------------------------------------+
|                   VIEWMODEL LAYER                   |
|                                                     |
|  PlanificadorViewModel                              |
|                                                     |
|  - Mantiene _estado: MutableStateFlow               |
|  - Aplica logica de negocio (consolidar, filtrar)   |
|  - Sobrevive rotaciones de pantalla                 |
|  - NO conoce la UI directamente                     |
+-------------------------+---------------------------+
                          |  lee / construye
                          v
+-----------------------------------------------------+
|                    MODEL LAYER                      |
|                                                     |
|  Receta        -> id, nombre, ingredientes          |
|  Ingrediente   -> nombre, cantidad, unidad          |
|  ItemCompra    -> nombre, cantidad, unidad          |
|  EstadoPlanificador -> snapshot inmutable del app   |
+-----------------------------------------------------+
```

**Flujo de datos reactivo:**

```
Usuario interactua con Composable
        |
        v
Composable llama metodo en ViewModel
        |
        v
ViewModel actualiza _estado (copia inmutable via .copy())
        |
        v
StateFlow emite nuevo valor
        |
        v
collectAsState() recompone el Composable afectado
```

---

## Estructura de carpetas

```
app/src/main/
|
+-- AndroidManifest.xml
|
+-- java/com/example/planificadordecomidas/
|   |
|   +-- MainActivity.kt                         # Punto de entrada; monta PlanificadorApp()
|   |
|   +-- modelo/                                 # Capa de datos (data classes puras)
|   |   +-- Receta.kt
|   |   +-- Ingrediente.kt
|   |   +-- ItemCompra.kt
|   |
|   +-- viewmodel/                              # Capa de logica y estado
|   |   +-- EstadoPlanificador.kt               # Snapshot del estado de la app
|   |   +-- PlanificadorViewModel.kt            # Logica de negocio y mutacion de estado
|   |
|   +-- ui/
|       +-- PlanificadorApp.kt                  # Composable raiz: Scaffold + NavHost
|       |
|       +-- navegacion/
|       |   +-- DestinoNavegacion.kt            # Sealed class con las 4 rutas
|       |   +-- AppNavHost.kt                   # Configuracion del NavHost y composables
|       |
|       +-- componentes/                        # Componentes reutilizables entre pantallas
|       |   +-- BarraBusqueda.kt                # Campo de texto para busqueda/filtrado
|       |   +-- BarraNavegacionInferior.kt      # NavigationBar con los 4 destinos
|       |   +-- TarjetaReceta.kt                # Card para mostrar una receta en lista
|       |
|       +-- pantallas/
|       |   +-- recetas/
|       |   |   +-- PantallaRecetas.kt          # Lista filtrable de recetas
|       |   |
|       |   +-- crear/
|       |   |   +-- PantallaCrear.kt            # Pantalla de alta; logica de validacion
|       |   |   +-- FormularioCrearReceta.kt    # Composable del formulario
|       |   |   +-- FilaIngredienteEditable.kt  # Fila de un ingrediente en el formulario
|       |   |   +-- IngredienteEditable.kt      # Data class local (estado de formulario)
|       |   |
|       |   +-- plansemanal/
|       |   |   +-- PantallaPlanSemanal.kt      # Asignacion de recetas por dia
|       |   |
|       |   +-- compras/
|       |       +-- PantallaCompras.kt          # Lista de compras consolidada
|       |       +-- ItemCompraFila.kt           # Fila con checkbox por ingrediente
|       |
|       +-- theme/
|       |   +-- Color.kt                        # Paleta de colores Material3
|       |   +-- Theme.kt                        # Tema de la aplicacion
|       |   +-- Type.kt                         # Tipografia
|       |
|       +-- utilidades/
|           +-- FormatoCantidad.kt              # Funcion de formato para cantidades numericas
|
+-- res/
    +-- values/
    |   +-- strings.xml                         # Cadenas de texto (incluye nombres_dias)
    |   +-- colors.xml
    |   +-- themes.xml
    +-- drawable/                               # Iconos del launcher
    +-- mipmap-*/                               # Variantes de densidad del icono
    +-- xml/
        +-- backup_rules.xml
        +-- data_extraction_rules.xml

app/src/test/
+-- viewmodel/
    +-- PlanificadorViewModelTest.kt            # 9 tests unitarios del ViewModel

app/src/androidTest/
+-- ExampleInstrumentedTest.kt                  # Test instrumentado de plantilla
```

---

## Modelos de datos

### `Receta`

Representa una receta de cocina. Es inmutable por ser `data class`.

| Campo          | Tipo                | Descripcion                                                                       |
|----------------|---------------------|-----------------------------------------------------------------------------------|
| `id`           | `Int`               | Identificador unico. El ViewModel lo asigna de forma monotona creciente.          |
| `nombre`       | `String`            | Nombre legible de la receta.                                                      |
| `ingredientes` | `List<Ingrediente>` | Lista de ingredientes requeridos para preparar la receta.                         |

---

### `Ingrediente`

Componente atomico de una receta. No tiene identificador propio; se identifica por su posicion en la lista de la receta contenedora.

| Campo      | Tipo     | Descripcion                                               |
|------------|----------|-----------------------------------------------------------|
| `nombre`   | `String` | Nombre del ingrediente (ej. "Tomate", "Aceite de oliva"). |
| `cantidad` | `Double` | Cantidad numerica requerida.                              |
| `unidad`   | `String` | Unidad de medida (ej. "gramos", "taza", "unidades").      |

---

### `ItemCompra`

Representa un ingrediente en la lista de compras consolidada. Estructuralmente identico a `Ingrediente` pero semanticamente diferente: la cantidad es la suma acumulada de todas las recetas del plan semanal que contienen ese ingrediente con la misma unidad.

| Campo      | Tipo     | Descripcion                                                                         |
|------------|----------|-------------------------------------------------------------------------------------|
| `nombre`   | `String` | Nombre normalizado del ingrediente (minusculas, sin espacios extremos).              |
| `cantidad` | `Double` | Suma total de cantidades de ese ingrediente en el plan semanal.                     |
| `unidad`   | `String` | Unidad de medida normalizada (minusculas, sin espacios extremos).                   |

---

### `EstadoPlanificador`

Snapshot inmutable de toda la UI state de la aplicacion. Es el unico objeto que fluye a traves del `StateFlow`. Contiene una propiedad computada que deriva la lista filtrada sin persistirla.

| Campo                 | Tipo               | Valor inicial                   | Descripcion                                                                                          |
|-----------------------|--------------------|---------------------------------|------------------------------------------------------------------------------------------------------|
| `recetas`             | `List<Receta>`     | Lista con una receta precargada | Catalogo completo de recetas disponibles.                                                            |
| `planSemanal`         | `List<Receta?>`    | Lista de 7 `null`               | Una entrada por dia (indice 0 = lunes, 6 = domingo). `null` indica dia sin receta.                   |
| `comprasConsolidadas` | `List<ItemCompra>` | `emptyList()`                   | Lista de compras derivada del plan semanal. Ordenada alfabeticamente.                                |
| `itemsComprados`      | `Set<String>`      | `emptySet()`                    | Conjunto de nombres normalizados de items ya marcados como comprados.                                |
| `textoBusqueda`       | `String`           | `""`                            | Texto del campo de busqueda por nombre en PantallaRecetas.                                           |
| `filtroIngrediente`   | `String`           | `""`                            | Texto del campo de filtro por ingrediente en PantallaRecetas.                                        |

**Propiedad computada:**

| Propiedad           | Tipo           | Descripcion                                                                                                          |
|---------------------|----------------|----------------------------------------------------------------------------------------------------------------------|
| `recetasFiltradas`  | `List<Receta>` | Deriva de `recetas` aplicando `textoBusqueda` y `filtroIngrediente` de forma combinada. Se recalcula en cada acceso. |

---

### `IngredienteEditable`

Data class local al paquete `crear`. Representa el estado mutable de una fila del formulario antes de convertirse en un `Ingrediente` validado. No forma parte del estado global.

| Campo      | Tipo     | Valor inicial | Descripcion                                                       |
|------------|----------|---------------|-------------------------------------------------------------------|
| `nombre`   | `String` | `""`          | Nombre del ingrediente ingresado en el formulario.                |
| `cantidad` | `String` | `""`          | Cantidad como texto (se convierte a `Double` al guardar).         |
| `unidad`   | `String` | `""`          | Unidad de medida ingresada en el formulario.                      |

---

## API del ViewModel

`PlanificadorViewModel` expone un unico `StateFlow` de solo lectura y ocho metodos publicos. Todos los metodos son sincronicos y producen un nuevo estado inmutable via `.copy()`.

### Estado observable

| Propiedad | Tipo                            | Descripcion                                                          |
|-----------|---------------------------------|----------------------------------------------------------------------|
| `estado`  | `StateFlow<EstadoPlanificador>` | Flujo de solo lectura que la UI observa con `collectAsState()`.      |

### Metodos publicos

| Metodo                                                                          | Parametros                                                                                        | Retorno            | Descripcion                                                                                                                                                                              |
|---------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `agregarReceta(receta: Receta)`                                                 | `receta: Receta`                                                                                  | `Unit`             | Agrega una receta al catalogo. No realiza ninguna accion si ya existe una receta con el mismo `id`.                                                                                      |
| `crearYAgregarReceta(nombre: String, ingredientes: List<Ingrediente>)`          | `nombre: String`, `ingredientes: List<Ingrediente>`                                               | `Unit`             | Asigna un `id` automatico (siguiente al maximo actual), construye la `Receta` y la agrega al catalogo. Es el metodo que invoca `PantallaCrear`.                                          |
| `eliminarReceta(idReceta: Int)`                                                 | `idReceta: Int`                                                                                   | `Unit`             | Elimina la receta del catalogo, la quita de todos los dias del plan semanal donde estuviera asignada, recalcula la lista de compras y limpia del set de comprados los items que ya no correspondan. |
| `asignarRecetaADia(indiceDia: Int, idReceta: Int)`                              | `indiceDia: Int` (0-6), `idReceta: Int`                                                           | `Unit`             | Asigna la receta indicada al dia de la semana. Recalcula lista de compras. Si `indiceDia` esta fuera del rango 0..6 o la receta no existe, no modifica el estado.                        |
| `limpiarDia(indiceDia: Int)`                                                    | `indiceDia: Int` (0-6)                                                                            | `Unit`             | Elimina la asignacion de receta para el dia indicado (establece `null`). Recalcula lista de compras. Si el indice es invalido, no modifica el estado.                                    |
| `actualizarEstadoComprado(nombreItem: String, estaComprado: Boolean)`           | `nombreItem: String`, `estaComprado: Boolean`                                                     | `Unit`             | Marca o desmarca un item de la lista de compras. El nombre se normaliza antes de operar. No hace nada si el item no existe en `comprasConsolidadas`.                                     |
| `actualizarTextoBusqueda(texto: String)`                                        | `texto: String`                                                                                   | `Unit`             | Actualiza el campo `textoBusqueda` del estado. La propiedad computada `recetasFiltradas` refleja el cambio inmediatamente.                                                               |
| `actualizarFiltroIngrediente(ingrediente: String)`                              | `ingrediente: String`                                                                             | `Unit`             | Actualiza el campo `filtroIngrediente` del estado. Se combina con `textoBusqueda` en el filtrado.                                                                                        |
| `obtenerListaComprasConsolidada(): List<ItemCompra>`                            | ninguno                                                                                           | `List<ItemCompra>` | Devuelve un snapshot de la lista de compras actual. Util en tests o en logica externa que necesite el valor sin suscribirse al flujo.                                                    |

---

## Flujo de datos

El siguiente diagrama describe el ciclo completo desde una accion del usuario hasta la actualizacion visual, tomando como ejemplo la asignacion de una receta a un dia del plan:

```
1. Usuario toca "Seleccionar receta" en un dia del plan
        |
        v
2. PantallaPlanSemanal actualiza estado local:
   indiceDiaSeleccionado = indice
        |
        v
3. SelectorRecetaDialog aparece y muestra estado.recetas
        |
        v
4. Usuario elige una receta en el dialogo
        |
        v
5. viewModel.asignarRecetaADia(indiceDia, idReceta) es invocado
        |
        v
6. PlanificadorViewModel:
   a. Busca la receta en estado.recetas
   b. Copia planSemanal e inserta la receta en el indice
   c. Llama consolidarCompras(planActualizado) ->
      agrupa ingredientes por (nombre_normalizado, unidad),
      suma cantidades, ordena alfabeticamente
   d. Llama filtrarItemsCompradosVigentes() ->
      descarta del Set los items que ya no esten en compras
   e. Emite nuevo EstadoPlanificador via _estado.value = ...
        |
        v
7. StateFlow emite el nuevo snapshot
        |
        v
8. PantallaPlanSemanal (collectAsState) se recompone:
   muestra el nombre de la receta recien asignada
        |
        v
9. PantallaCompras (collectAsState) se recompone:
   muestra la lista de compras actualizada con los
   nuevos ingredientes consolidados
```

**Principios clave de este flujo:**

- El estado nunca se muta directamente: siempre se crea una copia via `.copy()`.
- La lista de compras es completamente derivada: no existe como entrada del usuario sino como resultado de `consolidarCompras()`.
- El set de items comprados se limpia automaticamente en cada operacion que modifica el plan, garantizando que no queden checkmarks "huerfanos".
- Todos los composables que observan `estado` se recomponen de forma selectiva gracias al mecanismo de Compose.

---

## Navegacion

La navegacion esta implementada con **Navigation Compose**. El grafo tiene cuatro destinos planos (sin anidamiento ni argumentos de ruta). La pantalla de inicio es `recetas`.

### Destinos

| Objeto                          | Ruta (`ruta`)    | Titulo visible | Descripcion                                              |
|---------------------------------|------------------|----------------|----------------------------------------------------------|
| `DestinoNavegacion.Recetas`     | `"recetas"`      | Recetas        | Lista y busqueda de recetas. Destino de inicio.          |
| `DestinoNavegacion.Crear`       | `"crear"`        | Crear          | Formulario de alta de nueva receta.                      |
| `DestinoNavegacion.PlanSemanal` | `"plan_semanal"` | Plan Semanal   | Asignacion de recetas a los siete dias de la semana.     |
| `DestinoNavegacion.Compras`     | `"compras"`      | Compras        | Lista de compras consolidada con checkboxes.             |

### Como funciona la navegacion

**Estructura:** `PlanificadorApp` monta un `Scaffold` con `BarraNavegacionInferior` en `bottomBar` y `AppNavHost` como contenido principal. Ambos reciben el mismo `navController`.

**Barra inferior:** `BarraNavegacionInferior` itera `DestinoNavegacion.itemsBarraInferior` (lista de los cuatro destinos en orden). Al tocar un item navega con:
- `popUpTo(startDestinationId) { saveState = true }` — limpia el back stack hasta el inicio para evitar acumulacion.
- `launchSingleTop = true` — evita instancias duplicadas del mismo destino.
- `restoreState = true` — restaura el estado de scroll o posicion al volver a un destino.

**Navegacion programatica desde Recetas a Crear:** `PantallaRecetas` recibe `navController` y lo usa en el `FloatingActionButton` para navegar a `DestinoNavegacion.Crear.ruta` sin opciones especiales.

**Vuelta atras desde Crear:** `PantallaCrear` llama `navController.popBackStack()` tanto al guardar exitosamente como al cancelar, regresando siempre a `PantallaRecetas`.

**Diagrama de transiciones:**

```
                   +----------+
        +--------> |  Recetas | <--------+
        |          +----+-----+          |
        |               |                |
  popBackStack()    navegar("crear")  (barra inferior)
        |               |                |
        |          +----v-----+          |
        +--------- |  Crear   |          |
                   +----------+          |
                                         |
        +-- (barra inferior) ------------+
        |
   +----+----------+         +----------+
   |  Plan Semanal |         | Compras  |
   +---------------+         +----------+

  Todos los destinos son accesibles desde la barra inferior en todo momento.
```

---

## Como correr el proyecto

### Requisitos previos

| Herramienta          | Version minima recomendada            |
|----------------------|---------------------------------------|
| Android Studio       | Hedgehog (2023.1.1) o superior        |
| JDK                  | 11 (incluido en Android Studio)       |
| Android SDK          | API 24 (instalada via SDK Manager)    |
| Gradle               | Administrado por el wrapper incluido  |
| Dispositivo/emulador | Android 7.0 (API 24) o superior       |

### Pasos

1. **Clonar el repositorio:**

   ```bash
   git clone <url-del-repositorio>
   cd practico-1-planificador-comida
   ```

2. **Abrir en Android Studio:**

   Usar `File > Open` y seleccionar la carpeta raiz del proyecto. Android Studio detectara automaticamente el proyecto Gradle.

3. **Sincronizar dependencias:**

   Al abrir, Android Studio ejecuta la sincronizacion automaticamente. Si no lo hace, usar `File > Sync Project with Gradle Files`.

4. **Seleccionar dispositivo:**

   En la barra superior, abrir el selector de dispositivo y elegir un emulador existente o conectar un dispositivo fisico con depuracion USB habilitada.

5. **Ejecutar la aplicacion:**

   Presionar el boton Run (`Shift+F10`) o usar `Run > Run 'app'`.

   Alternativamente, desde la linea de comandos:

   ```bash
   ./gradlew installDebug
   ```

### Verificar la instalacion

Al iniciar la aplicacion debe mostrarse `PantallaRecetas` con la receta precargada "Ensalada simple". La barra de navegacion inferior debe mostrar las pestanas: Recetas, Crear, Plan Semanal y Compras.

---

## Como correr los tests

### Tests unitarios (JVM)

Los tests unitarios estan en `app/src/test/` y no requieren dispositivo ni emulador. Cubren la logica del `PlanificadorViewModel`.

**Desde Android Studio:**

Hacer clic derecho sobre el archivo `PlanificadorViewModelTest.kt` o sobre el paquete `viewmodel` y seleccionar `Run Tests`.

**Desde la linea de comandos:**

```bash
./gradlew test
```

El reporte HTML se genera en:

```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Tests instrumentados (Android)

Los tests instrumentados estan en `app/src/androidTest/` y requieren un dispositivo o emulador conectado.

**Desde la linea de comandos:**

```bash
./gradlew connectedAndroidTest
```

**Desde Android Studio:**

Hacer clic derecho sobre `ExampleInstrumentedTest.kt` y seleccionar `Run`.

### Casos de prueba existentes

Los nueve tests en `PlanificadorViewModelTest` cubren los siguientes escenarios:

| Test                                                          | Escenario verificado                                                           |
|---------------------------------------------------------------|--------------------------------------------------------------------------------|
| `inicia_con_receta_precargada_y_plan_vacio`                   | El estado inicial tiene 1 receta y 7 dias vacios.                              |
| `asignar_y_limpiar_dia_funciona_correctamente`                | `asignarRecetaADia` y `limpiarDia` modifican `planSemanal` como se espera.     |
| `eliminar_receta_tambien_limpia_su_dia_asignado`              | Al eliminar una receta, su dia asignado queda como `null`.                     |
| `lista_de_compras_consolida_por_nombre_normalizado`           | Ingredientes con el mismo nombre (distinto casing/espacios) suman cantidades.  |
| `asignar_dia_con_indice_invalido_no_modifica_estado`          | Indices 7 y -1 no provocan cambios en el estado.                               |
| `actualizar_estado_comprado_marca_y_desmarca_correctamente`   | El toggle de checkbox funciona en ambas direcciones.                           |
| `actualizar_estado_comprado_ignora_item_inexistente`          | Marcar un item que no existe en compras no modifica `itemsComprados`.          |
| `eliminar_receta_limpia_items_comprados_relacionados`         | Al eliminar una receta del plan, sus items comprados se eliminan del set.      |
| `filtrado_recetas_por_nombre_funciona_correctamente`          | `actualizarTextoBusqueda` filtra correctamente por nombre (case-insensitive).  |
| `filtrado_recetas_por_ingrediente_funciona_correctamente`     | `actualizarFiltroIngrediente` filtra recetas que contienen el ingrediente.     |

---

## Roadmap

Las siguientes mejoras estan identificadas como proximos pasos naturales para el proyecto:

### Persistencia de datos
- Integrar **Room** (base de datos SQLite) para que las recetas y el plan semanal sobrevivan al cierre de la aplicacion.
- Alternativamente, usar **DataStore** para persistir el estado serializado si la complejidad de un esquema relacional no es necesaria.

### Mejoras de UI
- Agregar iconos a los items de la barra de navegacion inferior (actualmente muestran solo texto).
- Implementar pantalla de detalle de receta con la lista completa de ingredientes y un boton de asignar al plan directamente.
- Soporte para edicion de recetas existentes (actualmente solo se permite crear y eliminar).
- Animaciones de transicion entre pantallas usando el soporte nativo de Navigation Compose.

### Funcionalidades del dominio
- Soporte para porciones: escalar las cantidades de los ingredientes segun el numero de comensales.
- Asignacion de recetas a momentos del dia (desayuno, almuerzo, cena) ademas del dia.
- Exportacion de la lista de compras como texto plano o compartir via `Intent`.
- Categorias o etiquetas en recetas para filtrado mas granular.

### Calidad tecnica
- Implementar inyeccion de dependencias con **Hilt** para facilitar el testing del ViewModel con repositorios mockeados.
- Extraer la logica de consolidacion de compras a un caso de uso (`UseCase`) independiente para mejorar la testeabilidad.
- Agregar tests de UI con **Compose Testing** para las pantallas principales.
- Configurar un pipeline de integracion continua (CI) con GitHub Actions que ejecute los tests en cada pull request.

### Accesibilidad
- Revisar y completar los `contentDescription` de todos los elementos interactivos (el `FloatingActionButton` ya lo tiene; verificar iconos de la barra y botones de accion).
- Probar con TalkBack para garantizar navegacion correcta por voz.
