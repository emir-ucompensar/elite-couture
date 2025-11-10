# 🔷 Supabase Tests

Scripts específicos para probar la integración con Supabase (PostgreSQL + Storage).

---

## 📄 Scripts Disponibles

### **`test_supabase_storage.py`** ✨ Principal
**Descripción:** Test completo de Supabase Storage (bucket `product-images`).

**Funcionalidad:**
- ✅ Verificación de conexión
- ✅ Upload de imágenes individuales
- ✅ Listar archivos en bucket
- ✅ Download de imágenes
- ✅ Generación de URLs públicas
- ✅ Batch upload (múltiples archivos)
- ✅ Cleanup opcional

**Uso:**
```bash
python supabase/test_supabase_storage.py
```

**Output esperado:**
```
======================================================================
  RESUMEN DE TESTS
======================================================================

Tests ejecutados: 6
Exitosos: 6
Fallidos: 0

RESULTADO: TODOS LOS TESTS PASARON
```

---

### **`diagnose_supabase.py`**
**Descripción:** Diagnóstico completo de la configuración de Supabase.

**Funcionalidad:**
- ✅ Verifica conexión a Supabase
- ✅ Verifica acceso al bucket `product-images`
- ✅ Lista archivos en bucket (con manejo de permisos)
- ✅ Genera URL pública de prueba
- ✅ Proporciona troubleshooting inteligente

**Uso:**
```bash
python supabase/diagnose_supabase.py
```

**Output esperado:**
```
[PASSED] Conexión establecida
[PASSED] Bucket 'product-images' es accesible
[INFO] Se encontraron X archivos en el bucket
[PASSED] URL pública generada correctamente
```

---

## 📋 Requisitos

- Python 3.8+
- `supabase>=2.0.0` - Cliente de Supabase
- `Pillow>=10.0.0` - Procesamiento de imágenes
- `python-dotenv` - Variables de entorno
- Archivo `.env` configurado

---

## 🔧 Configuración

### **1. Variables de Entorno**
El archivo `.env` en la raíz de `testing/` debe contener:
```env
SUPABASE_URL=https://tjhhqwizpiywyrwjpgrg.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### **2. Bucket Configuration**
- **Nombre:** `product-images`
- **Visibilidad:** Public
- **RLS Policies:** Habilitadas con acceso público para desarrollo

---

## 📊 Resultados de Tests

| Script | Tests | Pasados | Estado |
|--------|-------|---------|--------|
| `test_supabase_storage.py` | 6 | 6 | ✅ |
| `diagnose_supabase.py` | - | - | ✅ |

---

## 🎯 Características Probadas

### **Upload**
- Creación de imágenes de prueba (800x600 JPEG)
- Upload a carpeta `testing/`
- Verificación de éxito

### **Download**
- Descarga de imágenes desde bucket
- Verificación de dimensiones (800x600)
- Validación de formato (JPEG)

### **List**
- Listado de archivos en carpeta
- Verificación de metadata (tamaño, nombre)

### **Public URLs**
- Generación de URLs públicas
- Formato: `https://tjhhqwizpiywyrwjpgrg.supabase.co/storage/v1/object/public/product-images/...`

### **Batch Operations**
- Upload de 3 imágenes simultáneas
- Verificación de todas las subidas

---

## 🐛 Troubleshooting

### Error: "list_buckets() returns empty"
**Causa:** El anon key no tiene permisos de admin para listar buckets.  
**Solución:** Esto es esperado y correcto por seguridad. Usa acceso directo al bucket.

### Error: "Row-level security policy"
**Causa:** RLS policies no configuradas correctamente.  
**Solución:** Ejecuta el SQL de setup: `../design/supabase_schema.sql`

### Error: "getaddrinfo failed"
**Causa:** Sin conexión a internet o URL incorrecta.  
**Solución:** Verifica conexión y credenciales en `.env`

---

## 📚 Recursos

- **Supabase Dashboard:** https://supabase.com/dashboard/project/tjhhqwizpiywyrwjpgrg
- **Storage:** https://supabase.com/dashboard/project/tjhhqwizpiywyrwjpgrg/storage/buckets/product-images
- **Docs:** https://supabase.com/docs/guides/storage

---

*Última actualización: 10 de Noviembre, 2025*
