#!/usr/bin/env python3
"""
Elite Couture - ERD Generator
Genera un diagrama ERD en formato Mermaid desde DatabaseContract.kt
"""

import re
from pathlib import Path
from typing import List, Dict, Tuple

# Colores ANSI para terminal
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    MAGENTA = '\033[95m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header():
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}Elite Couture - ERD Generator{Colors.RESET}")
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}\n")

def extract_tables_from_contract(contract_path: Path) -> Dict[str, Dict]:
    """
    Extrae información de las tablas desde DatabaseContract.kt
    
    Returns:
        Dict con estructura: {
            'table_name': {
                'columns': [(name, type, constraints), ...],
                'foreign_keys': [(column, ref_table, ref_column), ...]
            }
        }
    """
    content = contract_path.read_text(encoding='utf-8')
    tables = {}
    
    # Buscar cada objeto de tabla (Users, Products, CartItems, Favorites)
    table_pattern = r'object\s+(\w+)\s*\{[^}]*?const val TABLE_NAME = "(\w+)"[^}]*?val CREATE_TABLE = """(.*?)"""'
    
    for match in re.finditer(table_pattern, content, re.DOTALL):
        object_name = match.group(1)
        table_name = match.group(2)
        create_sql = match.group(3).strip()
        
        # Extraer columnas
        columns = []
        column_pattern = r'(\w+)\s+(INTEGER|TEXT|REAL)([^,\n]*)'
        
        for col_match in re.finditer(column_pattern, create_sql):
            col_name = col_match.group(1)
            col_type = col_match.group(2)
            col_constraints = col_match.group(3).strip()
            
            # Determinar constraints importantes
            constraints_list = []
            if 'PRIMARY KEY' in col_constraints:
                constraints_list.append('PK')
            if 'UNIQUE' in col_constraints:
                constraints_list.append('UNIQUE')
            if 'NOT NULL' in col_constraints:
                constraints_list.append('NOT NULL')
            if 'AUTOINCREMENT' in col_constraints:
                constraints_list.append('AUTO')
            
            columns.append((col_name, col_type, ', '.join(constraints_list) if constraints_list else ''))
        
        # Extraer foreign keys (considerando interpolación de Kotlin)
        foreign_keys = []
        # Buscar todas las líneas con FOREIGN KEY
        fk_pattern = r'FOREIGN KEY \([^\)]+\) REFERENCES [^\)]+\)[^\n]*'
        
        for fk_match in re.finditer(fk_pattern, create_sql):
            fk_line = fk_match.group(0)
            
            # Extraer columna local: FOREIGN KEY ($COLUMN_USER_UUID) o FOREIGN KEY (user_uuid)
            local_col_match = re.search(r'FOREIGN KEY \(\$?(?:COLUMN_)?(\w+)\)', fk_line)
            if not local_col_match:
                continue
            local_col = local_col_match.group(1).lower()
            
            # Extraer tabla referenciada: ${Users.TABLE_NAME} o users
            ref_table_match = re.search(r'REFERENCES (?:\$\{(\w+)\.TABLE_NAME\}|(\w+))\(', fk_line)
            if not ref_table_match:
                continue
            ref_table = (ref_table_match.group(1) or ref_table_match.group(2)).lower()
            
            # Extraer columna referenciada: ${Users.COLUMN_UUID} o uuid
            ref_col_match = re.search(r'\((?:\$\{(?:\w+)\.COLUMN_(\w+)\}|(\w+))\)', fk_line.split('REFERENCES')[1])
            if not ref_col_match:
                continue
            ref_col = (ref_col_match.group(1) or ref_col_match.group(2)).lower()
            
            foreign_keys.append((local_col, ref_table, ref_col))
        
        tables[table_name] = {
            'columns': columns,
            'foreign_keys': foreign_keys
        }
    
    return tables

def generate_mermaid_erd(tables: Dict[str, Dict]) -> str:
    """
    Genera diagrama ERD en formato Mermaid
    """
    lines = [
        "erDiagram",
        ""
    ]
    
    # Definir cada tabla con sus columnas
    for table_name, table_info in tables.items():
        lines.append(f"    {table_name.upper()} {{")
        
        for col_name, col_type, constraints in table_info['columns']:
            # Mapear tipos SQL a tipos Mermaid
            mermaid_type = {
                'INTEGER': 'int',
                'TEXT': 'string',
                'REAL': 'float'
            }.get(col_type, col_type.lower())
            
            constraint_str = f" \"{constraints}\"" if constraints else ""
            lines.append(f"        {mermaid_type} {col_name}{constraint_str}")
        
        lines.append("    }")
        lines.append("")
    
    # Definir relaciones (foreign keys)
    for table_name, table_info in tables.items():
        for fk_column, ref_table, ref_column in table_info['foreign_keys']:
            # Determinar cardinalidad
            # cart_items y favorites: many-to-one con users y products
            if table_name in ['cart_items', 'favorites']:
                # Muchos items/favoritos pertenecen a un usuario/producto
                lines.append(f"    {ref_table.upper()} ||--o{{ {table_name.upper()} : \"has\"")
            else:
                lines.append(f"    {ref_table.upper()} ||--o{{ {table_name.upper()} : \"references\"")
    
    return '\n'.join(lines)

def generate_markdown_doc(tables: Dict[str, Dict], mermaid_erd: str) -> str:
    """
    Genera documentación completa en Markdown con el ERD
    """
    lines = [
        "# Elite Couture - Diagrama de Base de Datos",
        "",
        "Este documento fue generado automáticamente desde `DatabaseContract.kt`.",
        "",
        "## Diagrama ERD",
        "",
        "```mermaid",
        mermaid_erd,
        "```",
        "",
        "## Descripción de Tablas",
        ""
    ]
    
    # Documentar cada tabla
    table_descriptions = {
        'users': 'Almacena información de usuarios registrados y usuarios invitados.',
        'products': 'Catálogo de productos de la tienda con imágenes, precios y tags para filtrado.',
        'cart_items': 'Items en el carrito de compras de cada usuario con cantidad.',
        'favorites': 'Productos marcados como favoritos por cada usuario.'
    }
    
    for table_name, table_info in tables.items():
        lines.append(f"### `{table_name}`")
        lines.append("")
        lines.append(table_descriptions.get(table_name, 'Tabla de la base de datos.'))
        lines.append("")
        lines.append("| Columna | Tipo | Restricciones |")
        lines.append("|---------|------|---------------|")
        
        for col_name, col_type, constraints in table_info['columns']:
            constraints_display = constraints if constraints else '-'
            lines.append(f"| `{col_name}` | {col_type} | {constraints_display} |")
        
        lines.append("")
        
        # Mostrar foreign keys si existen
        if table_info['foreign_keys']:
            lines.append("**Foreign Keys:**")
            lines.append("")
            for fk_column, ref_table, ref_column in table_info['foreign_keys']:
                lines.append(f"- `{fk_column}` → `{ref_table}.{ref_column}`")
            lines.append("")
    
    lines.append("## Relaciones")
    lines.append("")
    lines.append("- **users** ↔ **cart_items**: Un usuario puede tener múltiples items en su carrito")
    lines.append("- **users** ↔ **favorites**: Un usuario puede tener múltiples productos favoritos")
    lines.append("- **products** ↔ **cart_items**: Un producto puede estar en múltiples carritos")
    lines.append("- **products** ↔ **favorites**: Un producto puede ser favorito de múltiples usuarios")
    lines.append("")
    lines.append("## Notas")
    lines.append("")
    lines.append("- Versión de base de datos: 7")
    lines.append("- Las columnas `images` y `tags` usan pipe (`|`) como separador")
    lines.append("- Las relaciones tienen `ON DELETE CASCADE` para integridad referencial")
    lines.append("")
    lines.append("---")
    lines.append("*Generado automáticamente con `generate_erd.py`*")
    
    return '\n'.join(lines)

def main():
    print_header()
    
    # Rutas (relativas a la raíz del proyecto)
    project_root = Path(__file__).parent.parent
    contract_path = project_root / "app/src/main/java/com/elitecouture/app/data/local/contract/DatabaseContract.kt"
    output_mermaid = project_root / "design/database_erd.mmd"
    output_markdown = project_root / "design/DATABASE_SCHEMA.md"
    
    # Verificar que existe DatabaseContract.kt
    if not contract_path.exists():
        print(f"{Colors.YELLOW}⚠ No se encontró {contract_path}{Colors.RESET}")
        print(f"{Colors.CYAN}Buscando archivo...{Colors.RESET}")
        
        # Buscar el archivo desde la raíz del proyecto
        possible_paths = list(project_root.rglob("DatabaseContract.kt"))
        if possible_paths:
            contract_path = possible_paths[0]
            print(f"{Colors.GREEN}✓ Encontrado: {contract_path}{Colors.RESET}\n")
        else:
            print(f"{Colors.YELLOW}✗ No se pudo encontrar DatabaseContract.kt{Colors.RESET}")
            return 1
    
    print(f"📂 Archivo origen: {contract_path}")
    print(f"📂 Salida Mermaid: {output_mermaid}")
    print(f"📂 Salida Markdown: {output_markdown}")
    print()
    
    # Extraer tablas
    print(f"{Colors.CYAN}Analizando DatabaseContract.kt...{Colors.RESET}")
    tables = extract_tables_from_contract(contract_path)
    
    if not tables:
        print(f"{Colors.YELLOW}✗ No se encontraron tablas en el archivo{Colors.RESET}")
        return 1
    
    print(f"{Colors.GREEN}✓ Encontradas {len(tables)} tablas:{Colors.RESET}")
    for table_name in tables.keys():
        column_count = len(tables[table_name]['columns'])
        fk_count = len(tables[table_name]['foreign_keys'])
        print(f"  - {table_name}: {column_count} columnas, {fk_count} foreign keys")
    print()
    
    # Generar diagrama Mermaid
    print(f"{Colors.CYAN}Generando diagrama ERD...{Colors.RESET}")
    mermaid_erd = generate_mermaid_erd(tables)
    
    # Crear directorio design si no existe
    output_mermaid.parent.mkdir(parents=True, exist_ok=True)
    
    # Guardar archivo .mmd
    output_mermaid.write_text(mermaid_erd, encoding='utf-8')
    print(f"{Colors.GREEN}✓ Diagrama Mermaid guardado: {output_mermaid}{Colors.RESET}")
    
    # Generar documentación Markdown
    print(f"{Colors.CYAN}Generando documentación...{Colors.RESET}")
    markdown_doc = generate_markdown_doc(tables, mermaid_erd)
    output_markdown.write_text(markdown_doc, encoding='utf-8')
    print(f"{Colors.GREEN}✓ Documentación guardada: {output_markdown}{Colors.RESET}")
    
    # Resumen
    print()
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}¡Generación completada!{Colors.RESET}")
    print()
    print(f"{Colors.CYAN}Puedes visualizar el diagrama en:{Colors.RESET}")
    print(f"  • GitHub: El archivo .md se renderiza automáticamente")
    print(f"  • VSCode: Instala extensión 'Markdown Preview Mermaid Support'")
    print(f"  • Online: https://mermaid.live/ (pega el contenido de .mmd)")
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    
    return 0

if __name__ == "__main__":
    import sys
    sys.exit(main())
