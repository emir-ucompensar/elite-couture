# Testing Scripts - Elite Couture

Scripts de Python para testing y debugging de la aplicación Elite Couture.

## Requisitos

- **Python 3.7+** instalado
- **ADB** (Android Debug Bridge) en el PATH
- Dispositivo Android conectado o emulador en ejecución
- USB Debugging habilitado

### Verificar instalación:

```bash
python --version
adb devices
```

---

## Scripts Disponibles

### 1. `test_favorites.py`

**Propósito:** Monitorear logs en tiempo real de la funcionalidad de favoritos.

**Uso:**
```bash
python testing/test_favorites.py
```

**Lo que hace:**
1. Limpia el buffer de logcat
2. Inicia la aplicación Elite Couture
3. Muestra logs filtrados con colores:
   - **Cyan:** FavoriteDao (operaciones de base de datos)
   - **Verde:** AddToFavoritesUseCase (añadir favoritos)
   - **Amarillo:** RemoveFromFavoritesUseCase (quitar favoritos)
   - **Magenta:** ProductListAdapter (eventos UI)

**Detener:** Presiona `Ctrl+C`

**Ejemplo de salida:**
```
==================================================
  LOGS EN TIEMPO REAL
==================================================

ProductListAdapter: bind() -> Product: Vestido (prod-001), isFavorite=false
FavoriteDao: isFavorite() -> userUuid=user-123, productUuid=prod-001, exists=false
FavoriteDao: addFavorite() -> userUuid=user-123, productUuid=prod-001, result=1
AddToFavoritesUseCase: invoke() -> result=1, success=true
```

---

### 2. `check_favorites_db.py` 

**Propósito:** Consultar directamente la base de datos SQLite para verificar favoritos.

**Uso:**
```bash
python testing/check_favorites_db.py
```

**Lo que muestra:**
1. **Tabla completa de favoritos** (con JOIN a productos)
2. **Estadísticas:**
   - Total de favoritos
   - Favoritos por usuario
3. **Top 5 productos más favoritos**
4. **Verificación de integridad:**
   - Foreign Keys habilitadas
   - Sin duplicados (restricción UNIQUE)

**Nota:** Requiere emulador o dispositivo rooteado para acceso directo a la DB.

**Ejemplo de salida:**
```
============================================================
  Contenido de la Tabla FAVORITES
============================================================

ID | User UUID | Product UUID | Product Name | Created At
----------------------------------------------------------------------------------
1  | user-123  | prod-001     | Vestido Elegante | 2025-11-06 14:30:45
2  | user-123  | prod-003     | Blazer Moderno   | 2025-11-06 14:31:12

============================================================
  Estadísticas de Favoritos
============================================================

Total de favoritos: 2

Favoritos por usuario:
  Juan Pérez: 2 favorito(s)

============================================================
  Productos Más Populares
============================================================

1. Vestido Elegante: 5 veces
2. Blazer Moderno: 3 veces
3. Pantalón Casual: 2 veces
```

---

### 3. `copy_product_images.py`

**Propósito:** Copiar imágenes de productos al dispositivo sin recompilar.

**Uso:**
```bash
python testing/copy_product_images.py
```

**Lo que hace:**
1. Busca el directorio `product_images/` en el proyecto
2. Copia todas las imágenes (`.avif`, `.webp`, `.jpg`, `.png`) al dispositivo
3. Configura permisos adecuados
4. Opcionalmente reinicia la app

**Útil para:**
- Actualizar imágenes durante desarrollo
- Testing con diferentes assets
- Probar nuevos productos sin rebuild

**Ejemplo de salida:**
```
==================================================
  Copy Product Images - Elite Couture
==================================================

✓ Dispositivo conectado
✓ Directorio de imágenes: /path/to/product_images

==================================================
  Copiando Imágenes de Productos
==================================================

Encontrados 7 directorios de productos

📁 product_01
  ✓ → 17097806_76_D2.avif
  ✓ → 17097806_76-99999999_01.avif
  ✓ → 17097806_76.avif

📁 product_02
  ✓ → 17067801_77_B.avif
  ...

============================================================
Resumen:
  Total de archivos: 21
  Exitosos: 21
```

---

## Flujo de Testing Recomendado

### Escenario 1: Testing de Favoritos

1. **Terminal 1:** Monitorear logs
   ```bash
   python testing/test_favorites.py
   ```

2. **En el dispositivo:**
   - Inicia sesión
   - Añade/quita favoritos
   - Observa logs en tiempo real

3. **Terminal 2:** Verificar DB
   ```bash
   python testing/check_favorites_db.py
   ```

### Escenario 2: Actualizar Imágenes

1. Agrega nuevas imágenes en `product_images/product_XX/`
2. Ejecuta:
   ```bash
   python testing/copy_product_images.py
   ```
3. Reinicia la app cuando se solicite

---

## Características de los Scripts

### Colores en Terminal
Todos los scripts usan códigos ANSI para mejor legibilidad:
- **Cyan:** Encabezados principales
- **Verde:** Operaciones exitosas
- **Amarillo:** Advertencias e información
- **Rojo:** Errores
- **Magenta:** Datos especiales

### Manejo de Errores
- Verificación de conexión ADB
- Mensajes claros de error
- Sugerencias de solución
- Salida limpia con `Ctrl+C`

### Multiplataforma
- Funciona en Windows, macOS, Linux
- Detección automática de rutas
- Comandos shell portables

---

## Troubleshooting

### "No se detectó ningún dispositivo"
```bash
# Verificar dispositivos conectados
adb devices

# Si no aparece nada, reinicia ADB
adb kill-server
adb start-server
adb devices
```

### "Error ejecutando query"
- **Causa:** No tienes acceso root
- **Solución:** Usa emulador o dispositivo rooteado para `check_favorites_db.py`
- **Alternativa:** Usa solo `test_favorites.py` para monitorear logs

### Scripts no ejecutan en PowerShell
```powershell
# Asegúrate de que Python está en el PATH
python --version

# Ejecuta con ruta completa si es necesario
python C:\ruta\completa\testing\test_favorites.py
```

### Colores no se muestran correctamente
```bash
# En Windows PowerShell moderno, debería funcionar
# Si no, usa Windows Terminal para mejor soporte ANSI
```

---

## Notas

- Los logs están en nivel **DEBUG (D)** - no aparecen en builds de producción
- `check_favorites_db.py` requiere acceso a `/data/data/` (emulador o root)
- Las imágenes se copian a `/data/data/com.elitecouture.app/files/product_images/`
- Todos los scripts tienen manejo de `Ctrl+C` para salida limpia

---

## Recursos

- [Documentación ADB](https://developer.android.com/studio/command-line/adb)
- [Python subprocess](https://docs.python.org/3/library/subprocess.html)
- [SQLite en Android](https://developer.android.com/training/data-storage/sqlite)

---

**Última actualización:** 2025-11-06  
**Python requerido:** 3.7+  
**ADB requerido:** Sí
