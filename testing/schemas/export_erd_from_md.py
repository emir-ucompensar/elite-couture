import os
# Colores ANSI para terminal
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    MAGENTA = '\033[95m'
    RED = '\033[91m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header():
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}Elite Couture - ERD to PNG Exporter{Colors.RESET}")
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}\n")

def print_success(msg):
    print(f"{Colors.GREEN}{msg}{Colors.RESET}")

def print_error(msg):
    print(f"{Colors.RED}{msg}{Colors.RESET}")

def print_info(msg):
    print(f"{Colors.CYAN}{msg}{Colors.RESET}")
#!/usr/bin/env python3
"""
Exporta el diagrama ERD a PNG a partir de un archivo DATABASE_SCHEMA.md con bloque mermaid.
"""
import os
import sys
import re
import tempfile
import subprocess
from pathlib import Path

def extract_mermaid_block(md_path):
    text = Path(md_path).read_text(encoding="utf-8")
    # Busca el primer bloque mermaid
    m = re.search(r'```mermaid\n([\s\S]+?)\n```', text, re.IGNORECASE)
    if not m:
        print("No se encontró bloque mermaid en el markdown.")
        sys.exit(1)
    block = m.group(1)
    # Normalizar tipos de datos para Mermaid ERD
    type_map = {
        'bigint': 'int',
        'integer': 'int',
        'int': 'int',
        'uuid': 'string',
        'string': 'string',
        'text': 'string',
        'character varying': 'string',
        'double precision': 'float',
        'float': 'float',
        'real': 'float',
        'array': 'string',
        'boolean': 'int',
        'timestamp with time zone': 'timestamp',
        'timestamp': 'timestamp',
    }
    def normalize_line(line):
        import re
        m = re.match(r'\s*([\w ]+)\s+([\w_]+)(.*)', line)
        if m:
            raw_type = m.group(1).strip().lower()
            name = m.group(2)
            rest = m.group(3)
            norm_type = type_map.get(raw_type, 'string')
            return f'        {norm_type} {name}{rest}'
        return line
    # Procesar cada línea del bloque
    lines = block.splitlines()
    out = []
    for l in lines:
        if re.match(r'\s*\w+\s*{', l) or re.match(r'\s*}', l) or l.strip() == '' or l.strip().startswith('erDiagram'):
            out.append(l)
        elif '||--' in l or '--o{' in l or '--o|' in l:
            out.append(l)
        else:
            out.append(normalize_line(l))
    return '\n'.join(out)

def main():
    print_header()
    # Detectar rutas relativas automáticamente
    base_dir = Path(__file__).parent.resolve()
    md_path = base_dir / "exports" / "DATABASE_SCHEMA.md"
    out_png = base_dir / "exports" / "database_erd_from_md.png"

    rel_md = os.path.relpath(md_path, start=os.getcwd())
    rel_png = os.path.relpath(out_png, start=os.getcwd())

    if not md_path.exists():
        print_error(f"✗ No se encontró {rel_md}")
        return 1

    print_info(f"Archivo Mermaid Markdown: {rel_md}")
    print_info(f"Salida PNG: {rel_png}\n")

    mermaid = extract_mermaid_block(md_path)
    # Guardar temporal
    with tempfile.NamedTemporaryFile(delete=False, suffix='.mmd', mode='w', encoding='utf-8') as tmp:
        tmp.write(mermaid.strip() + '\n')
        tmp_path = tmp.name

    # Verificar mmdc
    mmdc = None
    for candidate in ["mmdc.cmd", "mmdc"]:
        for p in os.environ.get("PATH", "").split(os.pathsep):
            exe = os.path.join(p, candidate)
            if os.path.isfile(exe):
                mmdc = exe
                break
        if mmdc:
            break

    def run_export(cmd):
        print_info(f"Ejecutando: {' '.join(cmd)}")
        completed = subprocess.run(cmd, capture_output=True, text=True)
        if completed.returncode == 0:
            print_success(f"✓ Diagrama exportado exitosamente!")
            print_success(f"{rel_png}")
        else:
            print_error("✗ Error al exportar:")
            print_error(completed.stderr)
            sys.exit(1)

    # Crear directorio de salida si no existe
    out_dir = os.path.dirname(out_png)
    if out_dir and not os.path.exists(out_dir):
        os.makedirs(out_dir)

    # Preferir mmdc global, si no usar npx
    if mmdc:
        cmd = [mmdc, '-i', str(tmp_path), '-o', str(out_png), '-b', 'white', '-t', 'default', '-w', '1920', '-H', '1080']
        run_export(cmd)
    else:
        print_info("No se encontró 'mmdc' global. Intentando con npx...")
        cmd = ['npx', '-p', '@mermaid-js/mermaid-cli', 'mmdc', '-i', str(tmp_path), '-o', str(out_png), '-b', 'white', '-t', 'default', '-w', '1920', '-H', '1080']
        run_export(cmd)
    os.remove(tmp_path)

    print()
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}¡Exportación completada!{Colors.RESET}")
    print()
    # Por seguridad, si llegara aquí, retornar 1 (no debería ocurrir)
    return 1

if __name__ == "__main__":
    import sys
    sys.exit(main())
