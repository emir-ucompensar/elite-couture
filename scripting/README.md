# 🧪 Testing Directory - Elite Couture# Testing Scripts - Elite Couture



Este directorio contiene todos los scripts de testing, validación y utilidades para el proyecto Elite Couture.Utilidades de Python para testing, debugging y empaquetado de la aplicación Elite Couture.



---## Requisitos



## 📁 Estructura de Directorios- Python 3.7 o superior

- ADB (Android Debug Bridge) en PATH - solo para scripts de testing de dispositivo

### **📂 `database/`** - Tests de Base de Datos- Dispositivo Android conectado o emulador en ejecución (para scripts ADB)

Scripts para probar la base de datos local (SQLite) y validar operaciones CRUD.- USB Debugging habilitado (para scripts ADB)



- **`check_favorites_db.py`** - Verifica el estado de la tabla de favoritosVerificar instalación:

- **`test_favorites.py`** - Tests unitarios para funcionalidad de favoritos```bash

- **`test_database_migration.py`** - ✨ **Tests completos de migración a Supabase (16 tests)**python --version

adb devices

### **📂 `supabase/`** - Tests de Supabase```

Scripts específicos para probar la integración con Supabase (PostgreSQL + Storage).

### Dependencias Adicionales para Supabase

- **`diagnose_supabase.py`** - Diagnóstico de conexión y configuración de Supabase

- **`test_supabase_storage.py`** - ✨ **Tests de Supabase Storage (upload, download, list, delete)**Para ejecutar los scripts de testing de Supabase:



### **📂 `images/`** - Procesamiento de Imágenes```bash

Scripts para copiar, procesar y organizar imágenes de productos.pip install -r testing/requirements_supabase.txt

```

- **`copy_product_images_v1.py`** - Primera versión del script de copia de imágenes

- **`copy_product_images_v2.py`** - Versión mejorada con manejo de erroresO instalar manualmente:

```bash

### **📂 `documentation/`** - Generación de Documentaciónpip install supabase Pillow python-dotenv

Scripts para generar documentación técnica automáticamente.```



- **`generate_erd.py`** - Genera diagramas ERD desde el schema de la base de datos## Scripts Disponibles

- **`export_erd_to_png.py`** - Exporta los ERD a formato PNG

### 1. package_source.py

### **📂 `utils/`** - Utilidades Generales

Scripts de utilidades y helpers reutilizables.Empaqueta el código fuente del proyecto en un archivo ZIP, excluyendo archivos de build, releases y datos privados.



- **`helper.py`** - Funciones auxiliares comunes**Uso:**

- **`package_source.py`** - Empaqueta el código fuente para releases```bash

python testing/package_source.py

---```



## 🚀 Inicio Rápido**Características:**

- Excluye directorios de build (build/, .gradle/), archivos compilados, imágenes fuente, Git, cache y archivos privados

### **1. Instalar dependencias**- Incluye código fuente (.kt, .java), recursos Android (.xml), configuración (.gradle, .json), scripts (.py) y documentación (.md)

```bash- Genera ZIP con timestamp en `releases/pending_review/`

pip install -r requirements_supabase.txt- Proporciona estadísticas de compresión

```

**Salida:** `releases/pending_review/elite-couture-source_YYYYMMDD_HHMMSS.zip`

### **2. Configurar credenciales**

El archivo `.env` ya está configurado con las credenciales de Supabase:### 2. test_favorites.py

```env

SUPABASE_URL=https://tjhhqwizpiywyrwjpgrg.supabase.coMonitorea logs en tiempo real de la funcionalidad de favoritos mediante logcat.

SUPABASE_ANON_KEY=eyJhbGc...

```**Uso:**

```bash

### **3. Ejecutar tests principales**python testing/test_favorites.py

```

#### **Test de Migración Completa (Recomendado)**

```bash**Características:**

python database/test_database_migration.py- Limpia el buffer de logcat antes de iniciar

```- Inicia automáticamente la aplicación

- ✅ Prueba CRUD de `users`, `products`, `cart_items`, `favorites`- Filtra y colorea logs de FavoriteDao, AddToFavoritesUseCase, RemoveFromFavoritesUseCase y ProductListAdapter

- ✅ 16 tests automatizados- Detener con Ctrl+C

- ✅ Verifica que Supabase esté funcionando correctamente

### 3. test_supabase_storage.py

#### **Test de Supabase Storage**

```bashScript completo de testing para Supabase Storage. Prueba todas las operaciones del bucket de imágenes.

python supabase/test_supabase_storage.py

```**Uso:**

- ✅ Prueba upload, download, list, delete de imágenes```bash

- ✅ Genera URLs públicas# Instalar dependencias primero

- ✅ Batch uploadpip install -r testing/requirements_supabase.txt



#### **Diagnóstico de Supabase**# Ejecutar tests

```bashpython testing/test_supabase_storage.py

python supabase/diagnose_supabase.py```

```

- ✅ Verifica conexión**Características:**

- ✅ Verifica acceso al bucket- Test de conexión con Supabase

- ✅ Genera URLs de prueba- Subida de imágenes (individual y batch)

- Listado de archivos en el bucket

---- Descarga y verificación de imágenes

- Generación de URLs públicas

## 📊 Estado de Tests- Eliminación de archivos

- Limpieza automática de archivos de prueba

### ✅ **Supabase Migration Tests** (16/16 PASADOS)- Reportes detallados con colores

```

USERS TABLE:       4/4 ✅**Tests incluidos:**

PRODUCTS TABLE:    5/5 ✅1. Conexión y verificación del bucket `product-images`

FAVORITES TABLE:   3/3 ✅2. Subida de imagen de prueba

CART_ITEMS TABLE:  4/4 ✅3. Listado de archivos en carpeta de testing

```4. Descarga y verificación de imagen

5. Obtención de URL pública

### ✅ **Supabase Storage Tests** (6/6 PASADOS)6. Subida de múltiples imágenes (batch)

```7. Limpieza opcional de archivos de prueba

Connection:     ✅

Upload:         ✅### 4. check_favorites_db.py

List:           ✅

Download:       ✅### 4. check_favorites_db.py

Public URL:     ✅

Batch Upload:   ✅Consulta directamente la base de datos SQLite para verificar el estado de favoritos.

```

**Uso:**

---```bash

python testing/check_favorites_db.py

## 🔧 Configuración Requerida```



### **Python 3.8+****Características:**

```bash- Muestra tabla completa de favoritos con JOIN a productos

python --version- Proporciona estadísticas (total, por usuario)

```- Lista top 5 productos más favoritos

- Verifica integridad de Foreign Keys

### **Dependencias**- Requiere emulador o dispositivo rooteado

- `supabase` - Cliente de Supabase para Python

- `python-dotenv` - Manejo de variables de entorno### 5. copy_product_images_v1.py / copy_product_images_v2.py

- `Pillow` - Procesamiento de imágenes

### 5. copy_product_images_v1.py / copy_product_images_v2.py

### **Archivos de Configuración**

- `.env` - Credenciales de Supabase (NO subir a Git)Copia imágenes de productos al dispositivo sin recompilar la aplicación.

- `requirements_supabase.txt` - Lista de dependencias Python

**Uso:**

---```bash

python testing/copy_product_images_v1.py

## 📝 Convenciones```



### **Naming****Características:**

- `test_*.py` - Scripts de testing automatizado- Busca directorio `product_images/` en el proyecto

- `check_*.py` - Scripts de verificación/diagnóstico- Copia imágenes (.avif, .webp, .jpg, .png) al almacenamiento interno del dispositivo

- `generate_*.py` - Scripts de generación de contenido- Configura permisos adecuados

- `*_v1.py`, `*_v2.py` - Versiones de scripts- Opción para reiniciar la aplicación



### **Output**### 6. helper.py

- `[PASSED]` - Test exitoso (verde)

- `[FAILED]` - Test fallido (rojo)Módulo de utilidades compartidas por los scripts de testing.

- `[INFO]` - Información general (cian)

- `[WARN]` - Advertencia (amarillo)**Contenido:**

- Funciones auxiliares para operaciones comunes con ADB

---- Manejo de errores centralizado

- Constantes compartidas

## 🎯 Próximos Tests a Agregar

## Troubleshooting

- [ ] **Test de autenticación** - Login, registro, JWT

- [ ] **Test de geolocalización** - Validar coordenadas de tiendas### No se detecta dispositivo

- [ ] **Test de cámara** - Mock de captura de fotos```bash

- [ ] **Test de sincronización** - SQLite ↔ Supabase syncadb devices

- [ ] **Performance tests** - Medir tiempos de respuestaadb kill-server

adb start-server

---```



## 📚 Documentación Adicional### Error ejecutando query (check_favorites_db.py)

Requiere emulador o dispositivo rooteado para acceso a `/data/data/`. Alternativa: usar `test_favorites.py` para monitorear logs.

- **`../SUPABASE_MIGRATION.md`** - Documentación completa de la migración

- **`../MIGRATION_SUMMARY.md`** - Resumen ejecutivo de la migración### Scripts no ejecutan en PowerShell

- **`../design/DATABASE_SCHEMA.md`** - Schema de la base de datosVerificar que Python esté en PATH:

```powershell

---python --version

```

## 🐛 Troubleshooting

## Notas

### **Error: "Could not find the table 'public.users'"**

→ Ejecuta el SQL en Supabase Dashboard: `../design/supabase_schema.sql`- Los logs de favoritos están en nivel DEBUG y no aparecen en builds de producción

- `check_favorites_db.py` requiere acceso root al sistema de archivos Android

### **Error: "getaddrinfo failed"**- Las imágenes se copian a `/data/data/com.elitecouture.app/files/product_images/`

→ Verifica tu conexión a internet y las credenciales en `.env`- `package_source.py` no requiere dispositivo Android conectado



### **Error: "Row-level security policy"**Última actualización: 2025-11-09

→ Verifica que las políticas RLS estén configuradas correctamente

---

## 👥 Contribuir

Al agregar nuevos scripts:

1. **Colócalos en la carpeta apropiada**
2. **Agrega documentación en el header**
3. **Usa colores ANSI para output**
4. **Implementa manejo de errores**
5. **Actualiza este README**

---

*Última actualización: 10 de Noviembre, 2025*
