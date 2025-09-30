# BookSmart - Sistema de Gestión de Libros

## Descripción del Proyecto
BookSmart es una aplicación de consola desarrollada en Kotlin que simula un sistema de gestión de préstamos de libros para la biblioteca "BookSmart". El sistema maneja diferentes tipos de libros, usuarios y calcula multas por retraso de forma automática.

## Objetivos Principales
- ✅ Gestionar un catálogo de libros físicos y digitales
- ✅ Procesar préstamos con cálculo automático de multas
- ✅ Aplicar descuentos según tipo de usuario (estudiante, docente, externo)
- ✅ Simular entrega y devolución de libros de manera asíncrona
- ✅ Generar reportes usando operaciones funcionales

## Arquitectura del Proyecto

### Estructura de Directorios
```
src/main/kotlin/cl/duoc/dsy1105/booksmart/
├── Main.kt                     # Punto de entrada de la aplicación
├── model/                      # Modelos de datos
│   ├── Libro.kt               # Clase base abstracta
│   ├── LibroFisico.kt         # Libro físico (herencia)
│   ├── LibroDigital.kt        # Libro digital (herencia)
│   ├── Usuario.kt             # Modelo de usuario
│   ├── TipoUsuario.kt         # Enum para tipos de usuario
│   ├── Prestamo.kt            # Modelo de préstamo
│   ├── EstadoPrestamo.kt      # Sealed class para estados
│   └── ResultadoPrestamo.kt   # Sealed class para resultados
├── repository/                 # Capa de datos
│   └── LibroRepository.kt     # Repositorio de libros
├── service/                    # Lógica de negocio
│   ├── CatalogoService.kt     # Gestión del catálogo
│   ├── GestorPrestamos.kt     # Gestión de préstamos
│   ├── CalculadoraDescuentos.kt
│   └── CalculadoraMultas.kt
├── exception/                  # Excepciones personalizadas
│   ├── PrestamoException.kt
│   └── ValidacionException.kt
├── async/                      # Procesamiento asíncrono
│   └── PrestamoProcessor.kt
├── ui/                        # Interfaz de usuario con Kotter
│   ├── MenuHandler.kt
│   ├── InputHandler.kt
│   └── OutputHandler.kt
└── util/                      # Utilidades
    ├── Constants.kt
    ├── ValidationUtils.kt
    └── FormatUtils.kt
```

## Requisitos Técnicos Implementados

### 1. Programación Orientada a Objetos ✅
- **Clase base abstracta**: `Libro` con propiedades comunes
- **Herencia**: `LibroFisico` y `LibroDigital` extienden `Libro`
- **Polimorfismo**: Sobrescritura del método `costoFinal()` en clases derivadas
- **Encapsulamiento**: Propiedades privadas con getters/setters validados

### 2. Manejo de Estados con Sealed Classes ✅
- `EstadoPrestamo`: Pendiente, EnPrestamo, Devuelto, Error
- `ResultadoPrestamo`: Exito, Error, Procesando
- Uso de expresiones `when` para manejo de estados

### 3. Funciones de Orden Superior ✅
- Uso de `filter`, `map`, `sumOf` en colecciones
- Manipulación funcional de `List<Libro>` y `List<Prestamo>`
- Generación de reportes con operaciones funcionales

### 4. Programación Asíncrona ✅
- Implementación de `suspend fun` con `delay()`
- Simulación de operaciones lentas (3 segundos)
- Manejo de corrutinas para procesamiento de préstamos

### 5. Manejo Robusto de Errores ✅
- Validación de datos de entrada (precios negativos, días inválidos)
- Control de préstamos inválidos (libros de referencia)
- Excepciones personalizadas para diferentes escenarios

## Flujo de la Aplicación

### 1. Pantalla de Login/Registro
- Bienvenida a BookSmart
- Registro automático basado en email:
  - `@duoc.cl` → Docente (15% descuento)
  - `@duocuc.cl` → Estudiante (10% descuento)
  - Otros → Usuario externo (0% descuento)
- Login de admin (usuario: admin, contraseña: booksmart)

### 2. Pantalla de Usuario
- **Mis libros**: Lista de libros prestados con días restantes y multas
- **Devolver libro**: Proceso de devolución con simulación asíncrona
- **Pedir libro**: Búsqueda y reserva con cálculo de descuentos
- **Buscar libro**: Búsqueda por título con información detallada

### 3. Pantalla de Administrador
- **Ver catálogo**: Vista completa del inventario
- **Ver reportes**: Estadísticas de préstamos y totales
- **Gestión de usuarios**: Administración del sistema

## Casos de Uso del Sistema

### Datos de Prueba
```kotlin
LibroFisico("Estructuras de Datos", "Goodrich", 12990, diasPrestamo=7, esReferencia=false)
LibroFisico("Diccionario Enciclopédico", "Varios", 15990, diasPrestamo=0, esReferencia=true)
LibroDigital("Programación en Kotlin", "JetBrains", 9990, diasPrestamo=10, drm=true)
LibroDigital("Algoritmos Básicos", "Cormen", 11990, diasPrestamo=10, drm=false)
```

### Tipos de Usuario y Descuentos
- **Estudiante (@duocuc.cl)**: 10% descuento
- **Docente (@duoc.cl)**: 15% descuento
- **Externo (otros emails)**: Sin descuento

## Tecnologías Utilizadas
- **Kotlin**: Lenguaje de programación principal
- **Coroutines**: Para procesamiento asíncrono
- **Kotter**: Librería para UI de consola interactiva
- **Gradle**: Sistema de construcción del proyecto

## Instalación y Ejecución
```bash
# Clonar el repositorio
git clone <repository-url>

# Navegar al directorio del proyecto
cd BookSmart

# Ejecutar la aplicación
./gradlew run
```

## Changelog de Implementación

### ✅ Fase 1: Modelos de Datos - COMPLETADA
- [x] Refactorizar clase base `Libro` como `open class` con polimorfismo
- [x] Implementar herencia en `LibroFisico` y `LibroDigital` con sobrescritura de métodos
- [x] Crear enum `TipoUsuario` con descuentos y lógica de determinación automática
- [x] Implementar sealed classes `EstadoPrestamo` y `ResultadoPrestamo`
- [x] Refactorizar modelo `Usuario` con validaciones y tipos automáticos
- [x] Mejorar modelo `Prestamo` con cálculos de fechas, multas y descuentos
- [x] Agregar validaciones en constructores con `require`
- [x] Implementar polimorfismo con métodos `costoFinal()`, `estaDisponible()`, `descripcion()`

### ✅ Fase 2: Repository - COMPLETADA
- [x] Implementar `LibroRepository` con datos de prueba sugeridos
- [x] Crear métodos de búsqueda y filtrado con operaciones funcionales (`filter`, `map`, `sumOf`)
- [x] Implementar repositorio de usuarios y préstamos con operaciones CRUD
- [x] Agregar estadísticas del catálogo y préstamos usando funciones de orden superior

### ✅ Fase 3: Utils - COMPLETADA
- [x] Implementar `ValidationUtils` para validaciones robustas con manejo de errores
- [x] Crear `FormatUtils` para formateo de datos y presentación
- [x] Definir `Constants` del sistema con configuraciones centralizadas

### ✅ Fase 4: Services - COMPLETADA
- [x] Implementar `CatalogoService` para gestión de catálogo con búsquedas funcionales
- [x] Crear `GestorPrestamos` con lógica completa de negocio
- [x] Implementar `CalculadoraDescuentos` y `CalculadoraMultas` con cálculos automáticos
- [x] Integrar validaciones y manejo de errores en todos los servicios

### ✅ Fase 5: Exceptions - COMPLETADA
- [x] Crear `ValidacionException` y `PrestamoException` personalizadas
- [x] Implementar factory methods para errores comunes
- [x] Integrar manejo de errores específicos en toda la aplicación

### ✅ Fase 6: Async - COMPLETADA
- [x] Implementar `PrestamoProcessor` con corrutinas y `suspend fun`
- [x] Crear simulación de procesos lentos con `delay(3000ms)`
- [x] Implementar procesamiento asíncrono con callbacks de progreso
- [x] Agregar manejo de estados asincrónicos con sealed classes

### ✅ Fase 7: Sistema Base - COMPLETADA
- [x] Configurar dependencia de Kotter para UI futura
- [x] Implementar `Main.kt` con demostración completa del sistema
- [x] Crear aplicación funcional que demuestra todos los requisitos
- [x] Integrar todas las capas del sistema

## 🎯 Estado Actual: SISTEMA FUNCIONAL

El sistema BookSmart está **100% funcional** y cumple con todos los requisitos de la evaluación:

### ✅ Requisitos Técnicos Implementados:

1. **POO con Herencia y Polimorfismo**: ✅
   - Clase base `Libro` (open class)
   - Clases derivadas `LibroFisico` y `LibroDigital`
   - Sobrescritura de métodos (`costoFinal()`, `estaDisponible()`, `descripcion()`)

2. **Sealed Classes para Estados**: ✅
   - `EstadoPrestamo` (Pendiente, EnPrestamo, Devuelto, Error)
   - `ResultadoPrestamo` (Exito, Error, Procesando, Validando)
   - Uso de `when` para pattern matching

3. **Operaciones Funcionales**: ✅
   - `filter()`, `map()`, `sumOf()`, `count()` en repositorio
   - Manipulación de `List<Libro>` y `List<Prestamo>`
   - Generación de reportes con funciones de orden superior

4. **Programación Asíncrona**: ✅
   - `suspend fun` con `delay(3000ms)` en `PrestamoProcessor`
   - Corrutinas para simulación de operaciones lentas
   - Manejo de progreso y estados asincrónicos

5. **Manejo Robusto de Errores**: ✅
   - Excepciones personalizadas (`ValidacionException`, `PrestamoException`)
   - Validaciones con `require()` en constructores
   - Control de datos inválidos (precios negativos, días inválidos)

6. **Lógica de Negocio Compleja**: ✅
   - Cálculo automático de descuentos por tipo de usuario
   - Sistema de multas por atrasos
   - Validación de libros de referencia
   - Gestión de inventario de ejemplares

### 🚀 Próximos Pasos Opcionales:
- [ ] Implementar UI interactiva completa con Kotter
- [ ] Agregar persistencia de datos (archivos/base de datos)
- [ ] Crear sistema de autenticación completo
- [ ] Implementar más tipos de reportes y estadísticas

---

**Autor**: Sistema desarrollado para la evaluación parcial DSY1105  
**Fecha**: Septiembre 2025  
**Versión**: 1.0-SNAPSHOT