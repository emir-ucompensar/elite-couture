# 📊 Database Tests

Scripts para probar las operaciones de base de datos (SQLite local y Supabase PostgreSQL).

---

## 📄 Scripts Disponibles

### **`test_database_migration.py`** ✨ Principal
**Descripción:** Test completo de migración a Supabase PostgreSQL.

**Funcionalidad:**
- ✅ CRUD completo de `users` (4 tests)
- ✅ CRUD completo de `products` (5 tests)
- ✅ CRUD completo de `favorites` (3 tests)
- ✅ CRUD completo de `cart_items` (4 tests)
- ✅ Cleanup automático de datos de prueba

**Uso:**
```bash
python database/test_database_migration.py
```

**Output esperado:**
```
======================================================================
  TEST SUMMARY
======================================================================

Tests ejecutados: 16
Exitosos: 16
Fallidos: 0

RESULTADO: ✅ TODOS LOS TESTS PASARON
La migración a Supabase está LISTA!
```

---

### **`check_favorites_db.py`**
**Descripción:** Verifica el estado de la tabla de favoritos en SQLite local.

**Funcionalidad:**
- Verifica estructura de la tabla
- Cuenta registros existentes
- Valida integridad de foreign keys

**Uso:**
```bash
python database/check_favorites_db.py
```

---

### **`test_favorites.py`**
**Descripción:** Tests unitarios para la funcionalidad de favoritos.

**Funcionalidad:**
- Test de agregar favorito
- Test de remover favorito
- Test de listar favoritos por usuario
- Test de verificar si producto es favorito

**Uso:**
```bash
python database/test_favorites.py
```

---

## 📋 Requisitos

- Python 3.8+
- `supabase` - Cliente de Supabase
- `python-dotenv` - Variables de entorno
- Archivo `.env` configurado con credenciales

---

## 🔧 Configuración

El archivo `.env` en la raíz de `testing/` debe contener:
```env
SUPABASE_URL=https://tjhhqwizpiywyrwjpgrg.supabase.co
SUPABASE_ANON_KEY=tu-anon-key
```

---

## 📊 Resultados de Tests

| Script | Tests | Pasados | Estado |
|--------|-------|---------|--------|
| `test_database_migration.py` | 16 | 16 | ✅ |
| `check_favorites_db.py` | - | - | ✅ |
| `test_favorites.py` | - | - | 📝 |

---

## 🐛 Troubleshooting

### Error: "Could not find the table"
→ Ejecuta el SQL de migración: `../design/supabase_schema.sql`

### Error: "Connection refused"
→ Verifica credenciales en `.env` y conexión a internet

---

*Última actualización: 10 de Noviembre, 2025*
