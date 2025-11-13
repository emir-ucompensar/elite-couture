# 🛠️ Utility Scripts

Scripts de utilidades generales y helpers reutilizables.

---

## 📄 Scripts Disponibles

### **`helper.py`**
**Descripción:** Funciones auxiliares comunes utilizadas por otros scripts.

**Funcionalidad:**
- Funciones de logging con colores
- Validación de archivos y directorios
- Utilidades de conversión
- Helpers para manejo de errores

**Uso:**
```python
from utils.helper import print_success, print_error

print_success("Operación exitosa!")
print_error("Algo salió mal")
```

**Funciones disponibles:**
- `print_success(msg)` - Imprime mensaje en verde
- `print_error(msg)` - Imprime mensaje en rojo
- `print_info(msg)` - Imprime mensaje en cian
- `print_warn(msg)` - Imprime mensaje en amarillo
- `validate_file(path)` - Verifica existencia de archivo
- `validate_dir(path)` - Verifica existencia de directorio

---

### **`package_source.py`** ✨ Principal
**Descripción:** Empaqueta el código fuente del proyecto en un archivo ZIP para entregas.

**Funcionalidad:**
- ✅ Empaqueta código fuente (`.kt`, `.java`, `.xml`)
- ✅ Incluye configuración (`.gradle`, `.json`)
- ✅ Incluye documentación (`.md`)
- ✅ Excluye builds, cache, imágenes, releases
- ✅ Excluye archivos privados (`.env`, `local.properties`)
- ✅ Genera timestamp en nombre del archivo
- ✅ Proporciona estadísticas de compresión

**Uso:**
```bash
python utils/package_source.py
```

**Output:**
```
releases/pending_review/elite-couture-source_YYYYMMDD_HHMMSS.zip
```

---

## 📋 Requisitos

- Python 3.8+
- Sin dependencias externas (usa módulos estándar)

---

## 🔧 Configuración

### **`package_source.py` - Exclusiones**

**Directorios excluidos:**
- `build/`, `.gradle/` - Archivos de compilación
- `releases/` - Releases anteriores
- `product_images/` - Imágenes fuente
- `.git/`, `.idea/` - Control de versiones e IDE
- `__pycache__/`, `*.pyc` - Cache de Python

**Archivos excluidos:**
- `.env` - Variables de entorno sensibles
- `local.properties` - Configuración local
- `*.apk`, `*.aab` - Builds compilados
- `*.keystore`, `*.jks` - Archivos de firma

**Incluidos:**
- `*.kt`, `*.java` - Código fuente
- `*.xml` - Recursos Android
- `*.gradle`, `*.json` - Configuración
- `*.md` - Documentación
- `*.py` - Scripts de testing

---

## 🎯 Casos de Uso

### **1. Entrega de Proyecto**
```bash
python utils/package_source.py
# → Genera ZIP para subir a plataforma de la universidad
```

### **2. Backup de Código**
```bash
python utils/package_source.py
# → Crea snapshot del código antes de cambios importantes
```

### **3. Code Review**
```bash
python utils/package_source.py
# → Empaqueta código para compartir con revisores
```

---

## 📊 Estadísticas

El script muestra:
- Número de archivos incluidos
- Tamaño total sin comprimir
- Tamaño del ZIP final
- Ratio de compresión
- Tiempo de ejecución

**Ejemplo de output:**
```
[INFO] Empaquetando proyecto...
[INFO] Archivos incluidos: 247
[INFO] Tamaño original: 2.4 MB
[INFO] Tamaño ZIP: 487 KB
[INFO] Compresión: 80%
[PASSED] ZIP creado: releases/pending_review/elite-couture-source_20251110_143025.zip
```

---

## 💡 Mejores Prácticas

### **Uso de `helper.py`**

1. **Importar al inicio:**
```python
from utils.helper import print_success, print_error, print_info
```

2. **Usar en lugar de print():**
```python
# ❌ Mal
print("Operación exitosa")

# ✅ Bien
print_success("Operación exitosa")
```

3. **Manejo de errores consistente:**
```python
try:
    resultado = operacion_riesgosa()
    print_success(f"Operación exitosa: {resultado}")
except Exception as e:
    print_error(f"Error: {str(e)}")
```

---

## 🐛 Troubleshooting

### Error: "Permission denied" al crear ZIP
→ Verifica que el directorio `releases/pending_review/` exista y tenga permisos de escritura.

### ZIP demasiado grande
→ Verifica que los directorios de build estén excluidos correctamente.

### Archivos faltantes en ZIP
→ Revisa las extensiones incluidas en `INCLUDED_EXTENSIONS`.

---

## 🔄 Extensión

Para agregar nuevas utilidades:

1. **Agregar función a `helper.py`:**
```python
def nueva_utilidad(param):
    """Descripción de la utilidad."""
    # Implementación
    pass
```

2. **Documentar en este README**

3. **Importar en otros scripts:**
```python
from utils.helper import nueva_utilidad
```

---

*Última actualización: 10 de Noviembre, 2025*
