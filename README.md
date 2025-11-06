# Elite Couture

## Aplicación Android de E-Commerce

Elite Couture es una aplicación académica Android que simula una plataforma moderna de comercio electrónico de moda. El proyecto demuestra principios de desarrollo nativo Android usando Android SDK, Gradle y Kotlin, con autenticación de usuarios, gestión de catálogo de productos y funcionalidad de favoritos.

---

## Objetivos del Proyecto

- Desarrollar una aplicación Android nativa desde cero usando Gradle y VS Code (sin Android Studio)
- Implementar operaciones CRUD completas para productos, usuarios y pedidos
- Diseñar una interfaz de usuario intuitiva, moderna y ligera compatible con Android 10+
- Aplicar las mejores prácticas en arquitectura Android y modularización de código

---

## Stack Tecnológico

| Categoría | Tecnología |
|-----------|------------|
| Lenguaje | Kotlin |
| Sistema de Build | Gradle 9.1.0 |
| SDK Mínimo | Android 10 (API 29) |
| SDK Objetivo | Android 34 |
| IDE de Desarrollo | Visual Studio Code |
| Control de Versiones | Git + GitHub |
| Base de Datos | SQLite |
| Carga de Imágenes | Coil 2.5.0 |
| Componentes UI | Material Design 3 |

---

## Comenzando

### Clonar el Repositorio

```bash
git clone https://github.com/emir-ucompensar/elite-couture.git
cd elite-couture
```

### Alternativa: Descargar desde Releases

Descarga el código fuente directamente desde la sección [Releases](https://github.com/emir-ucompensar/elite-couture/releases).

### Compilar la Aplicación

Compila el APK de debug:

```bash
gradle assembleDebug
```

Ubicación del output:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## Instalación y Pruebas

1. Inicia tu Emulador Android o conecta un dispositivo físico con depuración USB habilitada
2. Instala el APK generado:

   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. Lanza la aplicación desde el menú de apps de tu dispositivo

---

## Funcionalidades

### Implementadas

| Funcionalidad | Estado | Descripción |
|---------------|--------|-------------|
| Sistema de Autenticación | Completo | Login, registro y modo invitado con gestión de sesiones |
| Perfil de Usuario | Completo | Edición completa de perfil con validación y cifrado de dirección |
| Sistema de Favoritos | Completo | Añadir/eliminar favoritos con swipe-to-delete y funcionalidad de deshacer |
| Catálogo de Productos | Completo | Navegar productos con filtros por género y categoría |
| Diálogos Personalizados | Completo | Sistema de diálogos con identidad visual de marca |
| Modo Invitado | Completo | Acceso limitado con restricciones visuales |
| Seguridad | Completo | Cifrado AES-256-CBC para datos sensibles |
| Base de Datos | Completo | SQLite con migraciones y datos de prueba |

### En Desarrollo

| Funcionalidad | Estado | Descripción |
|---------------|--------|-------------|
| Carrito de Compras | Planificado | Añadir items, modificar cantidades, flujo de checkout |
| Historial de Pedidos | Planificado | Ver compras pasadas y detalles de pedidos |
| Búsqueda de Productos | Planificado | Búsqueda de texto completo en el catálogo |

---

## Arquitectura

### Estructura del Proyecto

```
app/
├── data/
│   ├── local/
│   │   ├── dao/              # Objetos de acceso a datos
│   │   ├── entity/           # Entidades de base de datos
│   │   └── contract/         # Contratos de base de datos
│   ├── repository/           # Repositorios de datos
│   ├── seed/                 # Datos de prueba
│   └── session/              # Gestión de sesiones
├── domain/
│   ├── model/                # Modelos de negocio
│   └── usecase/              # Casos de uso de lógica de negocio
│       ├── auth/             # Casos de uso de autenticación
│       ├── favorites/        # Casos de uso de favoritos
│       ├── product/          # Casos de uso de productos
│       └── profile/          # Casos de uso de perfil
├── ui/
│   ├── common/               # Componentes UI compartidos
│   │   ├── base/             # Clases base
│   │   └── extension/        # Funciones de extensión
│   └── feature/              # Módulos de características
│       ├── auth/             # Pantallas de autenticación
│       ├── favorites/        # Pantalla de favoritos
│       ├── profile/          # Pantallas de perfil
│       └── store/            # Pantallas de tienda/catálogo
└── util/                     # Clases de utilidad

```

### Esquema de Base de Datos (Versión 5)

**Tabla Users**
- Almacena información de usuarios con identificación basada en UUID
- Hash de contraseñas para seguridad
- Soporte para modo invitado

**Tabla Products**
- Catálogo de productos con soporte para múltiples imágenes
- Controles de visibilidad para invitados
- Clasificación por categoría y género

**Tabla Favorites**
- Asociaciones usuario-producto con timestamps
- Restricciones de clave foránea con eliminación en cascada
- Restricción única para prevenir duplicados

---

## Sistema de Diálogos Personalizados

Elite Couture cuenta con un sistema unificado de diálogos (`EliteCoutureDialog`) que mantiene una identidad visual consistente en toda la aplicación.

### Características Principales
- Tipografía Elite Sans Semibold en todos los botones
- Texto en mayúsculas con espaciado de letras optimizado (0.05)
- Botones subrayados para mejor accesibilidad
- Tamaño compacto profesional (12sp)
- Color de marca consistente (#560E2D)
- Soporte para hasta 3 botones (Positivo/Neutral/Negativo)

### Ejemplo de Uso

```kotlin
EliteCoutureDialog.create(requireContext())
    .setTitle(R.string.dialog_title)
    .setMessage(R.string.dialog_message)
    .setPositiveButton(R.string.button_save) {
        saveChanges()
    }
    .setNeutralButton(R.string.button_discard) {
        discardChanges()
    }
    .setNegativeButton(R.string.button_cancel)
    .show()
```

Documentación completa: [DIALOG_SYSTEM.md](design/DIALOG_SYSTEM.md)

---

## Funcionalidad de Favoritos

### Detalles de Implementación

El sistema de favoritos incluye:

**Detección Manual de Gestos**
- Implementación personalizada de swipe-to-reveal-delete
- GestureDetectorCompat para diferenciar tap/swipe
- Umbral de 110dp para revelar
- Cierre instantáneo al abrir otra tarjeta
- Detección de toque externo multicapa

**Operaciones CRUD**
- Añadir productos a favoritos con feedback visual
- Eliminar con gesto de swipe y opción de deshacer
- Persistencia entre sesiones
- Actualizaciones de UI en tiempo real

**Componentes UI**
- Layout horizontal compacto con imágenes de 80dp
- Cards de Material Design 3 con elevación
- Snackbar con acción de navegación
- Estado vacío con llamada a la acción

---

## Testing y Debugging

El proyecto incluye scripts de Python para testing y debugging:

### Scripts Disponibles

| Script | Descripción | Uso |
|--------|-------------|-----|
| `test_favorites.py` | Monitorea logs de favoritos en tiempo real | `python testing/test_favorites.py` |
| `check_favorites_db.py` | Consulta la base de datos de favoritos | `python testing/check_favorites_db.py` |
| `copy_product_images.py` | Copia imágenes sin recompilar | `python testing/copy_product_images.py` |
| `helper.py` | Muestra información del dispositivo y comandos útiles | `python testing/helper.py` |

**Requisitos:** Python 3.7+ y ADB instalado

Documentación completa: [testing/README.md](testing/README.md)

---
---

## Sistema de Diálogos Personalizados

Elite Couture cuenta con un sistema unificado de diálogos (`EliteCoutureDialog`) que mantiene la identidad visual en toda la app:

### Características
- **Tipografía Elite Sans Semibold** en todos los botones
- **Texto en MAYÚSCULAS** con letter-spacing optimizado (0.05)
- **Subrayado en botones** para mejor accesibilidad
- **Tamaño profesional** (12sp) compacto y legible
- **Color de marca consistente** (#560E2D)
- **Soporte para 3 botones** (Positivo/Neutral/Negativo)

### Ejemplo de uso
```kotlin
EliteCoutureDialog.create(requireContext())
    .setTitle(R.string.dialog_title)
    .setMessage(R.string.dialog_message)
    .setPositiveButton(R.string.button_save) {
        saveChanges()
    }
    .setNeutralButton(R.string.button_discard) {
        discardChanges()
    }
    .setNegativeButton(R.string.button_cancel)
    .show()
```

📖 **Documentación completa**: Ver [DIALOG_SYSTEM.md](design/DIALOG_SYSTEM.md)

---

## Testing y Debugging

El proyecto incluye scripts de Python para facilitar el testing y debugging:

### Scripts Disponibles

| Script | Descripción | Uso |
|--------|-------------|-----|
| `test_favorites.py` | Monitorea logs de favoritos en tiempo real | `python testing/test_favorites.py` |
| `check_favorites_db.py` | Consulta la base de datos de favoritos | `python testing/check_favorites_db.py` |
| `copy_product_images.py` | Copia imágenes al dispositivo sin recompilar | `python testing/copy_product_images.py` |
| `helper.py` | Muestra info del dispositivo y comandos útiles | `python testing/helper.py` |

**Requisitos:** Python 3.7+ y ADB instalado

**Documentación completa**: Ver [testing/README.md](testing/README.md)

---

## Decisiones de Desarrollo

### ¿Por qué sin Android Studio?

Este proyecto está construido completamente usando VS Code y herramientas de línea de comandos de Gradle para:
- Comprender la mecánica subyacente del sistema de build
- Obtener conocimiento profundo de la configuración de Gradle
- Demostrar competencia en desarrollo Android por línea de comandos
- Reducir dependencia en características específicas del IDE

### Patrones de Arquitectura

- **Patrón Repository**: Separa fuentes de datos de la lógica de negocio
- **Patrón Use Case**: Encapsula operaciones de negocio individuales
- **Service Locator**: Inyección de dependencias simplificada para contexto académico
- **MVVM Ligero**: ViewModel para estado de UI sin complejidad de LiveData/Flow

---

## Contribuciones

Este es un proyecto académico. Las contribuciones son bienvenidas con propósitos educativos.

### Estilo de Código
- Seguir convenciones de código Kotlin
- Usar nombres significativos para variables y funciones
- Documentar lógica compleja con comentarios
- Mantener funciones pequeñas y enfocadas

---

## Licencia

Licencia MIT - Libre para uso educativo

---

## Créditos

Proyecto académico desarrollado por **Emir** en **UCompensar**

---

**Elite Couture** - Desarrollo Android Moderno en la Práctica
