# 🖼️ Image Processing & Migration Scripts

Scripts para procesar, migrar y gestionar imágenes de productos en Supabase Storage.

---

## 🚀 Scripts de Migración a Supabase (NUEVOS)

### **`upload_all_product_images.py`** ⭐ PRINCIPAL
**Descripción:** Script de migración completo para subir todas las imágenes a Supabase Storage.

**Funcionalidad:**
- 🔄 Escanea automáticamente todas las carpetas de productos (product_01 a product_25)
- 📤 Sube todas las imágenes al bucket de Supabase
- 🎨 Convierte formatos AVIF a JPG automáticamente
- 📦 Optimiza imágenes (máx 500KB, calidad 85)
- 🗂️ Organiza en rutas descriptivas: `products/product_01/image_1.jpg`
- 💾 Genera archivo JSON con todas las URLs públicas
- 📊 Estadísticas detalladas de la migración

**Requisitos:**
```bash
pip install supabase Pillow pillow-avif-plugin python-dotenv
```

**Uso:**
```bash
cd testing/images
python upload_all_product_images.py
```

**Output:** Genera `uploaded_images_urls.json` con todas las URLs públicas.

---

### **`migrate_products_to_supabase.py`** ⭐ PRINCIPAL
**Descripción:** Migra todos los productos con sus datos y URLs de imágenes a la base de datos.

**Funcionalidad:**
- 📦 Crea 25 productos de moda femenina realistas
- 🔗 Asigna URLs de imágenes desde el archivo JSON
- 💰 Incluye precios, stock, categorías y tags
- 🎯 Inserta en tabla `products` de Supabase
- 📊 Genera resumen completo de migración

**Requisitos:**
```bash
pip install supabase python-dotenv
```

**Uso:**
```bash
# IMPORTANTE: Ejecutar DESPUÉS de upload_all_product_images.py
cd testing/images
python migrate_products_to_supabase.py
```

**Output:** Genera `migration_summary.json` con detalles de productos insertados.

---

## � Flujo de Migración Completo

### Paso 1: Subir Imágenes
```bash
python upload_all_product_images.py
```
- ✅ Convierte y optimiza todas las imágenes
- ✅ Las sube a Supabase Storage bucket `product-images`
- ✅ Genera `uploaded_images_urls.json`

### Paso 2: Migrar Productos
```bash
python migrate_products_to_supabase.py
```
- ✅ Lee las URLs del paso anterior
- ✅ Crea productos en la base de datos
- ✅ Asigna las URLs correctas a cada producto
- ✅ Genera `migration_summary.json`

### Paso 3: Verificar
```bash
# Verificar en Supabase Dashboard
# O ejecutar tests
cd testing/database
python test_database_migration.py
```

---

## 📄 Scripts Legacy (Copia Local)

### **`copy_product_images_v1.py`**
**Descripción:** Primera versión del script de copia de imágenes (LOCAL).

**Funcionalidad:**
- Copia imágenes desde directorio fuente
- Organiza en estructura `product_01/`, `product_02/`, etc.
- Convierte formatos si es necesario

**Uso:**
```bash
python images/copy_product_images_v1.py
```

---

### **`copy_product_images_v2.py`**
**Descripción:** Versión mejorada con manejo robusto de errores (LOCAL).

**Funcionalidad:**
- ✅ Copia imágenes con validación
- ✅ Manejo de errores mejorado
- ✅ Logging detallado
- ✅ Verificación de integridad
- ✅ Soporte para múltiples formatos (JPEG, PNG, WebP, AVIF)

**Uso:**
```bash
python images/copy_product_images_v2.py
```

---

## 📋 Requisitos Completos

### Para Migración a Supabase:
```bash
pip install -r ../requirements_supabase.txt
pip install pillow-avif-plugin
```

### Librerías Necesarias:
- `supabase` - Cliente de Supabase
- `Pillow` - Procesamiento de imágenes
- `pillow-avif-plugin` - Soporte para formato AVIF
- `python-dotenv` - Variables de entorno

---

## 🔧 Configuración

### **Estructura de Directorios**

**Input:** (Directorio fuente de imágenes)
```
source_images/
├── imagen1.jpg
├── imagen2.png
└── ...
```

**Output:** (Organizado por producto)
```
product_images/
├── product_01/
│   ├── 17097806_76_D2.avif
│   ├── 17097806_76-99999999_01.avif
│   └── 17097806_76.avif
├── product_02/
│   └── ...
└── ...
```

---

## 📊 Formatos Soportados

| Formato | Extensión | Soporte |
|---------|-----------|---------|
| JPEG | `.jpg`, `.jpeg` | ✅ |
| PNG | `.png` | ✅ |
| WebP | `.webp` | ✅ |
| AVIF | `.avif` | ✅ |
| GIF | `.gif` | ⚠️ Limitado |

---

## 🎯 Características

### **Validación**
- Verifica que los archivos sean imágenes válidas
- Detecta archivos corruptos
- Valida dimensiones mínimas

### **Conversión**
- Convierte automáticamente entre formatos
- Optimiza tamaño sin pérdida de calidad
- Mantiene metadata EXIF (opcional)

### **Organización**
- Estructura consistente por producto
- Nomenclatura estandarizada
- Índice de productos generado

---

## 💡 Uso Recomendado

1. **Preparar imágenes fuente** en un directorio
2. **Ejecutar script v2** para procesamiento
3. **Verificar output** en `product_images/`
4. **Subir a Supabase Storage** (opcional)

```bash
# Procesar imágenes
python images/copy_product_images_v2.py

# Verificar resultado
ls -R product_images/
```

---

## 🐛 Troubleshooting

### Error: "Pillow not installed"
```bash
pip install Pillow
```

### Error: "Image file is truncated"
→ Archivo corrupto. Reemplaza con imagen válida.

### Error: "Permission denied"
→ Verifica permisos de lectura/escritura en directorios.

---

*Última actualización: 10 de Noviembre, 2025*
