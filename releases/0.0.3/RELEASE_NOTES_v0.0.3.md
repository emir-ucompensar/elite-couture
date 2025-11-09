# Elite Couture - Alpha 0.0.3

**Versión Alpha 0.0.3** del proyecto académico Elite Couture.

Esta actualización introduce funcionalidades clave de e-commerce que transforman la aplicación en una plataforma de compras completamente funcional. Se implementa el sistema de carrito de compras, filtrado avanzado por categorías, y se expande significativamente el catálogo de productos.

Forma parte del desarrollo progresivo del curso de **Desarrollo de Aplicaciones Móviles Nativas**.

---

## 📋 Descripción del Proyecto

Elite Couture continúa consolidándose como una aplicación de tienda online de moda, enfocada en un diseño moderno, limpio y elegante. Esta versión introduce el núcleo funcional del sistema de e-commerce:

### 🆕 Nuevas Funcionalidades

- **🛒 Sistema de Carrito de Compras**
  - Agregar productos al carrito desde el catálogo
  - Incrementar/decrementar cantidades con botones +/-
  - Eliminar items del carrito
  - Persistencia de datos entre sesiones
  - Cálculo automático de totales

- **🏷️ Sistema de Tags y Filtrado**
  - Tags visuales en cada producto (Formal, Casual, Deportivo, etc.)
  - Filtrado por categorías desde el menú lateral
  - Búsqueda normalizada sin acentos
  - Indicador visual de filtro activo
  - Toggle de filtros (click para activar/desactivar)

- **📦 Catálogo Expandido**
  - **25 productos disponibles** (expandido desde 7)
  - **75 imágenes de productos** (3 por producto)
  - Nuevas categorías:
    - 👔 Jerseys (Hombre)
    - 🧥 Sacos (Hombre)
    - 👗 Conjuntos (Mujer)
  - Productos para ambos géneros balanceados

- **🎨 Mejoras de UI/UX**
  - Menú lateral drawer con categorías organizadas
  - Badges de tags con diseño vertical y opacidad
  - Navegación mejorada entre pantallas
  - Íconos Material Design actualizados

### 📊 Documentación Técnica

- **Diagrama ERD de Base de Datos**
  - Generación automática con script Python
  - Exportación a PNG de alta calidad
  - Documentación completa en Markdown
  - Visualización de 4 tablas relacionadas

### 🛠️ Herramientas de Desarrollo

- Scripts Python para procesamiento de imágenes
- Generador automático de ERD desde código Kotlin
- Exportador de diagramas a PNG

---

## 📱 Pantallas Disponibles

- 🔐 **Login** – Autenticación de usuarios
- 🔐 **Registro** – Creación de cuentas
- 🛍️ **Tienda** – Catálogo con filtrado y tags
- 🛒 **Carrito** – Gestión de compras
- ⭐ **Favoritos** – Productos guardados (con swipe-to-delete)
- 👤 **Perfil** – Gestión de información personal

---

## 📦 Contenido de la Release

- `EliteCouture-v0.0.3-debug.apk` → APK compilado listo para instalar
- `elite-couture-source-v0.0.3.zip` → Código fuente completo del proyecto

---

## 🚀 Cómo Ejecutar

### Opción 1: Instalar APK directamente

```bash
adb install EliteCouture-v0.0.3-debug.apk
```

### Opción 2: Compilar desde código fuente

1. Descarga y descomprime `elite-couture-source-v0.0.3.zip`
2. Abre la carpeta raíz en VS Code o Android Studio
3. Ejecuta:

```bash
gradle assembleDebug
```

El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📊 Estadísticas de la Versión

- **155 archivos modificados**
- **+3,480 líneas de código añadidas**
- **25 productos en catálogo**
- **4 tablas en base de datos**
- **6 pantallas funcionales**

---

## 🔧 Stack Tecnológico

- **Lenguaje:** Kotlin
- **Build System:** Gradle 9.1.0
- **SDK Mínimo:** Android 10 (API 29)
- **SDK Target:** Android 34
- **Base de Datos:** SQLite (v7)
- **UI:** Material Design 3
- **Imágenes:** Coil 2.5.0

---

## 📝 Notas de la Versión

### Base de Datos v7
- Nueva tabla `cart_items` para carrito de compras
- Columna `tags` agregada a tabla `products`
- Relaciones de foreign keys con ON DELETE CASCADE
- Migraciones automáticas implementadas

### Mejoras de Rendimiento
- Normalización de texto para búsquedas más rápidas
- Filtrado en memoria para mejor UX
- Carga lazy de imágenes con Coil

### Conocimientos Aplicados
- Arquitectura limpia (Clean Architecture)
- Patrón Repository
- Use Cases para lógica de negocio
- Material Design 3 guidelines
- Gestión de estado de UI

---

## 🐛 Problemas Conocidos

- El flujo de checkout está en desarrollo
- Historial de pedidos pendiente de implementación
- Búsqueda por texto no disponible (solo filtros por categoría)

---

## 🎓 Contexto Académico

Proyecto desarrollado como parte del curso de **Desarrollo de Aplicaciones Móviles Nativas** en **UCompensar**.

**Autor:** Emir  
**Repositorio:** [github.com/emir-ucompensar/elite-couture](https://github.com/emir-ucompensar/elite-couture)

---

**Elite Couture** - Desarrollo Android Moderno en la Práctica 🚀
