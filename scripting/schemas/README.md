# 📚 Documentation Generation Scripts

Scripts para generar documentación técnica automáticamente.

---

## 📄 Scripts Disponibles

### **`generate_erd.py`** ✨ Principal
**Descripción:** Genera diagramas ERD (Entity Relationship Diagram) desde el schema de la base de datos.

**Funcionalidad:**
- ✅ Lee el schema desde `DatabaseContract.kt`
- ✅ Genera diagrama Mermaid ERD
- ✅ Crea archivo `.mmd` para visualización
- ✅ Genera documentación en Markdown
- ✅ Incluye relaciones y foreign keys

**Uso:**
```bash
python documentation/generate_erd.py
```

**Output:**
- `design/database_erd.mmd` - Diagrama Mermaid
- `design/DATABASE_SCHEMA.md` - Documentación completa

---

### **`export_erd_to_png.py`**
**Descripción:** Exporta los diagramas ERD a formato PNG para documentación.

**Funcionalidad:**
- Convierte `.mmd` a PNG
- Genera imágenes de alta resolución
- Incluye en documentación

**Uso:**
```bash
python documentation/export_erd_to_png.py
```

**Requisitos adicionales:**
- Mermaid CLI (`mmdc`)

**Instalación de Mermaid CLI:**
```bash
npm install -g @mermaid-js/mermaid-cli
```

---

## 📋 Requisitos

### **Python**
- Python 3.8+
- Sin dependencias externas para `generate_erd.py`

### **Node.js** (solo para PNG export)
- Node.js 14+
- Mermaid CLI

---

## 🔧 Uso Típico

### **Generar documentación completa:**

1. **Generar ERD desde código:**
```bash
python documentation/generate_erd.py
```

2. **Exportar a PNG (opcional):**
```bash
python documentation/export_erd_to_png.py
```

3. **Verificar output:**
```bash
cat design/DATABASE_SCHEMA.md
open design/database_erd.png
```

---

## 📊 Output Generado

### **`database_erd.mmd`**
Diagrama Mermaid con formato:
```mermaid
erDiagram
    USERS {
        int COLUMN_ID "PK, AUTO"
        string COLUMN_UUID "UNIQUE, NOT NULL"
        string COLUMN_EMAIL "UNIQUE, NOT NULL"
        ...
    }
    PRODUCTS ||--o{ CART_ITEMS : "has"
```

### **`DATABASE_SCHEMA.md`**
Documentación estructurada:
- Diagrama ERD embebido
- Descripción de cada tabla
- Columnas con tipos y restricciones
- Relaciones entre tablas
- Foreign keys
- Versión de database

---

## 🎯 Características

### **Auto-detection**
- Detecta automáticamente tablas en `DatabaseContract.kt`
- Extrae columnas y tipos de datos
- Identifica claves primarias y foreign keys

### **Mermaid Syntax**
- Compatible con GitHub Markdown
- Renderiza en VS Code con extensión Mermaid
- Exportable a PNG/SVG

### **Documentación Rica**
- Formato Markdown profesional
- Tablas con formato
- Notas y advertencias
- Links a código fuente

---

## 💡 Casos de Uso

### **1. Documentación de Proyecto**
Genera documentación actualizada del schema para entrega de proyecto.

### **2. Onboarding**
Ayuda a nuevos desarrolladores a entender la estructura de la BD.

### **3. Refactoring**
Visualiza relaciones antes de hacer cambios en el schema.

### **4. Presentaciones**
Exporta diagramas PNG para incluir en slides.

---

## 🐛 Troubleshooting

### Error: "DatabaseContract.kt not found"
→ Ejecuta desde la raíz del proyecto o ajusta path en el script.

### Error: "mmdc command not found"
→ Instala Mermaid CLI:
```bash
npm install -g @mermaid-js/mermaid-cli
```

### Diagrama no renderiza en GitHub
→ GitHub soporta Mermaid nativamente. Verifica sintaxis.

---

## 📚 Recursos

- **Mermaid Docs:** https://mermaid.js.org/
- **ERD Syntax:** https://mermaid.js.org/syntax/entityRelationshipDiagram.html
- **VS Code Extension:** Mermaid Preview

---

*Última actualización: 10 de Noviembre, 2025*
