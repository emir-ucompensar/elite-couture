# Testing Scripts - Elite Couture

Utilidades de Python para testing, debugging y empaquetado de la aplicación Elite Couture.

## Requisitos

- Python 3.7 o superior
- ADB (Android Debug Bridge) en PATH - solo para scripts de testing de dispositivo
- Dispositivo Android conectado o emulador en ejecución (para scripts ADB)
- USB Debugging habilitado (para scripts ADB)

Verificar instalación:
```bash
python --version
adb devices
```

## Scripts Disponibles

### 1. package_source.py

Empaqueta el código fuente del proyecto en un archivo ZIP, excluyendo archivos de build, releases y datos privados.

**Uso:**
```bash
python testing/package_source.py
```

**Características:**
- Excluye directorios de build (build/, .gradle/), archivos compilados, imágenes fuente, Git, cache y archivos privados
- Incluye código fuente (.kt, .java), recursos Android (.xml), configuración (.gradle, .json), scripts (.py) y documentación (.md)
- Genera ZIP con timestamp en `releases/pending_review/`
- Proporciona estadísticas de compresión

**Salida:** `releases/pending_review/elite-couture-source_YYYYMMDD_HHMMSS.zip`

### 2. test_favorites.py

Monitorea logs en tiempo real de la funcionalidad de favoritos mediante logcat.

**Uso:**
```bash
python testing/test_favorites.py
```

**Características:**
- Limpia el buffer de logcat antes de iniciar
- Inicia automáticamente la aplicación
- Filtra y colorea logs de FavoriteDao, AddToFavoritesUseCase, RemoveFromFavoritesUseCase y ProductListAdapter
- Detener con Ctrl+C

### 3. check_favorites_db.py

Consulta directamente la base de datos SQLite para verificar el estado de favoritos.

**Uso:**
```bash
python testing/check_favorites_db.py
```

**Características:**
- Muestra tabla completa de favoritos con JOIN a productos
- Proporciona estadísticas (total, por usuario)
- Lista top 5 productos más favoritos
- Verifica integridad de Foreign Keys
- Requiere emulador o dispositivo rooteado

### 4. copy_product_images_v1.py / copy_product_images_v2.py

Copia imágenes de productos al dispositivo sin recompilar la aplicación.

**Uso:**
```bash
python testing/copy_product_images_v1.py
```

**Características:**
- Busca directorio `product_images/` en el proyecto
- Copia imágenes (.avif, .webp, .jpg, .png) al almacenamiento interno del dispositivo
- Configura permisos adecuados
- Opción para reiniciar la aplicación

### 5. helper.py

Módulo de utilidades compartidas por los scripts de testing.

**Contenido:**
- Funciones auxiliares para operaciones comunes con ADB
- Manejo de errores centralizado
- Constantes compartidas

## Troubleshooting

### No se detecta dispositivo
```bash
adb devices
adb kill-server
adb start-server
```

### Error ejecutando query (check_favorites_db.py)
Requiere emulador o dispositivo rooteado para acceso a `/data/data/`. Alternativa: usar `test_favorites.py` para monitorear logs.

### Scripts no ejecutan en PowerShell
Verificar que Python esté en PATH:
```powershell
python --version
```

## Notas

- Los logs de favoritos están en nivel DEBUG y no aparecen en builds de producción
- `check_favorites_db.py` requiere acceso root al sistema de archivos Android
- Las imágenes se copian a `/data/data/com.elitecouture.app/files/product_images/`
- `package_source.py` no requiere dispositivo Android conectado

Última actualización: 2025-11-09
