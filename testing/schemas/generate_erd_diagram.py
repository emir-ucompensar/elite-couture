#!/usr/bin/env python3
"""
Elite Couture - Generador Interactivo de ERD y Documentación
Basado en la experiencia de add_product_interactive.py
"""
import os
import sys
import shutil
from pathlib import Path
import subprocess

# Colores ANSI
class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    MAGENTA = '\033[95m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_success(text):
    print(f"{Colors.GREEN}{text}{Colors.RESET}")

def print_error(text):
    print(f"{Colors.RED}{text}{Colors.RESET}")

def print_warning(text):
    print(f"{Colors.YELLOW}{text}{Colors.RESET}")

def print_info(text):
    print(f"{Colors.CYAN}{text}{Colors.RESET}")

def print_step(text):
    print(f"{Colors.BLUE}▶{Colors.RESET} {text}")

def print_header(text):
    print(f"\n{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{text.center(80)}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}\n")

def menu():
    print_header("ELITE COUTURE - GENERADOR ERD Y DOCUMENTACIÓN")
    print("Selecciona una opción:")
    print(f"  {Colors.BOLD}1.{Colors.RESET} Generar database_erd.mmd desde CSVs")
    print(f"  {Colors.BOLD}2.{Colors.RESET} Generar DATABASE_SCHEMA.md desde Mermaid")
    print(f"  {Colors.BOLD}3.{Colors.RESET} Exportar imagen PNG del ERD (requiere MD generado)")
    print(f"  {Colors.BOLD}4.{Colors.RESET} Ejecutar TODO (1, 2 y 3 en serie)")
    print(f"  {Colors.BOLD}5.{Colors.RESET} Salir\n")
    return input("Opción: ").strip()

def generate_md_from_mermaid():
    """Genera DATABASE_SCHEMA.md a partir de database_erd.mmd"""
    base_dir = Path(__file__).parent
    export_dir = base_dir / "exports"
    export_dir.mkdir(exist_ok=True)
    mmd_path = export_dir / "database_erd.mmd"
    md_path = export_dir / "DATABASE_SCHEMA.md"

    if not mmd_path.exists():
        print_error(f"No se encontró {mmd_path}")
        return

    print_step("Leyendo archivo Mermaid...")
    mermaid = mmd_path.read_text(encoding="utf-8")

    # Extraer tablas y columnas del bloque mermaid
    import re
    table_blocks = re.findall(r"([A-Z0-9_]+)\s*{([^}]*)}", mermaid, re.MULTILINE)
    tables = []
    for table_name, cols_block in table_blocks:
        columns = []
        for line in cols_block.strip().splitlines():
            line = line.strip()
            if not line:
                continue
            # Ejemplo: string COLUMN_UUID "UNIQUE, NOT NULL"
            m = re.match(r'(\w+)\s+(\w+)(?:\s+"([^"]*)")?', line)
            if m:
                col_type, col_name, col_constraints = m.groups()
                columns.append({
                    'name': col_name,
                    'type': col_type,
                    'constraints': col_constraints or "-"
                })
        tables.append({'name': table_name, 'columns': columns})

    # Relaciones (líneas fuera de los bloques de tabla)
    rel_lines = [l for l in mermaid.splitlines() if '||--' in l or '--o{' in l or '--o|' in l]

    # Diccionario para descripciones de tablas
    table_desc = {
        'USERS': 'Almacena información de usuarios registrados y usuarios invitados.',
        'PRODUCTS': 'Catálogo de productos de la tienda con imágenes, precios y tags para filtrado.',
        'CART_ITEMS': 'Items en el carrito de compras de cada usuario con cantidad.',
        'FAVORITES': 'Productos marcados como favoritos por cada usuario.'
    }

    # Diccionario para notas de relaciones
    rel_desc = [
        '- **users** ↔ **cart_items**: Un usuario puede tener múltiples items en su carrito',
        '- **users** ↔ **favorites**: Un usuario puede tener múltiples productos favoritos',
        '- **products** ↔ **cart_items**: Un producto puede estar en múltiples carritos',
        '- **products** ↔ **favorites**: Un producto puede ser favorito de múltiples usuarios'
    ]

    # Diccionario para claves foráneas (solo para tablas que las tienen)
    fk_map = {
        'CART_ITEMS': [
            ('user_uuid', 'users.uuid'),
            ('product_uuid', 'products.uuid')
        ],
        'FAVORITES': [
            ('user_uuid', 'users.uuid'),
            ('product_uuid', 'products.uuid')
        ]
    }

    # Diccionario para tipos de columna (para la tabla markdown)
    type_map = {
        'int': 'INTEGER',
        'string': 'TEXT',
        'float': 'REAL',
        'double': 'REAL',
        'boolean': 'INTEGER',
    }

    # Generar markdown
    md = """# Elite Couture - Diagrama de Base de Datos\n\nEste documento fue generado automáticamente a partir de los datos en Supabase.\n\n## Diagrama ERD\n\n```mermaid\n""" + mermaid.strip() + "\n```\n\n## Descripción de Tablas\n"

    for t in tables:
        tname = t['name'].lower()
        md += f"\n### `{tname}`\n\n"
        md += table_desc.get(t['name'], '') + "\n\n"
        md += "| Columna | Tipo | Restricciones |\n|---------|------|---------------|\n"
        for col in t['columns']:
            coltype = type_map.get(col['type'].lower(), col['type'].upper())
            md += f"| `{col['name']}` | {coltype} | {col['constraints']} |\n"
        # Foreign keys
        if t['name'] in fk_map:
            md += "\n**Foreign Keys:**\n\n"
            for src, dst in fk_map[t['name']]:
                md += f"- `{src}` → `{dst}`\n"

    md += "\n## Relaciones\n\n"
    for r in rel_desc:
        md += r + "\n"

    md += "\n## Notas\n\n- Versión de base de datos: 7\n- Las columnas `images` y `tags` usan pipe (`|`) como separador\n- Las relaciones tienen `ON DELETE CASCADE` para integridad referencial\n\n---\n*Generado automáticamente con `generate_erd.py`*\n"

    md_path.write_text(md, encoding="utf-8")
    print_success(f"Archivo generado: {md_path}")

def export_png_from_mermaid():
    """Exporta la imagen PNG usando export_erd_from_md.py"""
    base_dir = Path(__file__).parent
    script_path = base_dir / "export_erd_from_md.py"
    if not script_path.exists():
        print_error(f"No se encontró el script de exportación: {script_path}")
        return False
    print_step("Exportando imagen PNG desde Markdown...")
    # Ejecutar el script de exportación
    result = subprocess.run([sys.executable, str(script_path)], cwd=base_dir, text=True)
    return result.returncode == 0


def generate_mermaid_from_csv():
    """Genera database_erd.mmd a partir de los CSV de tablas y relaciones."""
    base_dir = Path(__file__).parent
    exports_dir = base_dir / "exports"
    tables_csv = exports_dir / "elite-couture-foreign-key-mapping-tables.csv"
    rels_csv = exports_dir / "elite-couture-foreign-key-mapping-relations.csv"
    mmd_path = exports_dir / "database_erd.mmd"

    if not tables_csv.exists() or not rels_csv.exists():
        print_error("No se encontraron los CSV de tablas o relaciones en exports/")
        return

    import csv
    # Leer tablas y columnas
    tables = {}
    with tables_csv.open(encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            t = row["table_name"].upper()
            if row["type"] == "COLUMN":
                tables.setdefault(t, []).append(row)

    # Leer relaciones
    relations = []
    with rels_csv.open(encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if row["type"] == "FK":
                relations.append(row)

    # Mapeo de tipos y restricciones para el formato esperado
    type_map = {
        'INTEGER': 'int',
        'TEXT': 'string',
        'REAL': 'float',
        'FLOAT': 'float',
        'DOUBLE': 'float',
        'BOOLEAN': 'int',
    }

    # Mapeo de restricciones para mostrar en el bloque mermaid
    def build_constraints(row):
        constraints = []
        if row.get('is_primary_key', '').upper() == 'YES':
            constraints.append('PK')
        if row.get('is_auto_increment', '').upper() == 'YES':
            constraints.append('AUTO')
        if row.get('is_unique', '').upper() == 'YES':
            constraints.append('UNIQUE')
        if row.get('is_nullable', '').upper() == 'NO':
            constraints.append('NOT NULL')
        return ', '.join(constraints) if constraints else None

    # Generar Mermaid
    lines = ["erDiagram\n"]
    for t, cols in tables.items():
        lines.append(f"    {t} {{")
        for col in cols:
            coltype = type_map.get(col["data_type"].upper(), col["data_type"].lower())
            name = col["column_name"]
            constraints = build_constraints(col)
            if constraints:
                lines.append(f'        {coltype} {name} "{constraints}"')
            else:
                lines.append(f'        {coltype} {name}')
        lines.append("    }\n")

    # Relaciones: agrupar por tipo de relación y mostrar como en el ejemplo
    # Ejemplo: USERS ||--o{ CART_ITEMS : "has"
    for rel in relations:
        src = rel["referenced_table"].upper()
        dst = rel["table_name"].upper()
        # Determinar tipo de relación (por defecto "has")
        label = '"has"'
        lines.append(f"    {src} ||--o{{ {dst} : {label}")

    mmd_path.write_text("\n".join(lines), encoding="utf-8")
    print_success(f"Archivo Mermaid generado: {mmd_path}")

def main():
    while True:
        opt = menu()
        if opt == "1":
            generate_mermaid_from_csv()
        elif opt == "2":
            generate_md_from_mermaid()
        elif opt == "3":
            export_png_from_mermaid()
        elif opt == "4":
            # Ejecutar TODO (1, 2 y 3 en serie, solo resumen)
            steps = [
                ("Generar database_erd.mmd desde CSVs", generate_mermaid_from_csv),
                ("Generar DATABASE_SCHEMA.md desde Mermaid", generate_md_from_mermaid),
                ("Exportar imagen PNG del ERD", None)  # Usaremos llamada especial
            ]
            results = []
            import io
            import contextlib
            for idx, (label, func) in enumerate(steps):
                buf = io.StringIO()
                ok = False
                try:
                    if idx < 2:
                        with contextlib.redirect_stdout(buf), contextlib.redirect_stderr(buf):
                            func()
                        ok = True
                    else:
                        # Paso 3: exportador externo, suprimir salida
                        base_dir = Path(__file__).parent
                        script_path = base_dir / "export_erd_from_md.py"
                        out_png = base_dir / "exports" / "database_erd_from_md.png"
                        if not script_path.exists():
                            ok = False
                        else:
                            result = subprocess.run([sys.executable, str(script_path)], cwd=base_dir, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
                            # Success si el archivo PNG existe
                            ok = out_png.exists()
                except Exception:
                    ok = False
                results.append((label, ok))
            print_header("RESUMEN DE OPERACIONES TODO")
            for i, (label, ok) in enumerate(results, 1):
                if ok:
                    print(f"  {Colors.BOLD}{i}.{Colors.RESET} {label} [{Colors.GREEN}Success{Colors.RESET}]")
                else:
                    print(f"  {Colors.BOLD}{i}.{Colors.RESET} {label} [{Colors.RED}Failed{Colors.RESET}]")
            print()
        elif opt == "5":
            print_info("¡Hasta luego!")
            break
        else:
            print_warning("Opción no válida. Intenta de nuevo.")

if __name__ == "__main__":
    main()
