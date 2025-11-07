#!/usr/bin/env python3
"""
Elite Couture - ERD to PNG Exporter
Convierte el diagrama Mermaid a imagen PNG usando mermaid-cli (mmdc)
"""

import subprocess
import sys
from pathlib import Path

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

def check_mmdc_installed():
    """Verifica si mermaid-cli (mmdc) está instalado"""
    try:
        result = subprocess.run(['mmdc', '--version'], 
                              capture_output=True, 
                              text=True, 
                              check=False)
        if result.returncode == 0:
            version = result.stdout.strip()
            print(f"{Colors.GREEN}✓ mermaid-cli encontrado: {version}{Colors.RESET}")
            return True
    except FileNotFoundError:
        pass
    
    print(f"{Colors.YELLOW}✗ mermaid-cli (mmdc) no está instalado{Colors.RESET}\n")
    return False

def install_mmdc_instructions():
    """Muestra instrucciones de instalación"""
    print(f"{Colors.CYAN}Para instalar mermaid-cli, ejecuta:{Colors.RESET}\n")
    print(f"{Colors.MAGENTA}# Opción 1: Con npm (Node.js){Colors.RESET}")
    print(f"  npm install -g @mermaid-js/mermaid-cli\n")
    print(f"{Colors.MAGENTA}# Opción 2: Con npx (sin instalación global){Colors.RESET}")
    print(f"  npx -p @mermaid-js/mermaid-cli mmdc --version\n")
    print(f"{Colors.CYAN}Después vuelve a ejecutar este script.{Colors.RESET}\n")

def export_to_png(input_file: Path, output_file: Path, use_npx: bool = False):
    """
    Exporta el diagrama Mermaid a PNG
    
    Args:
        input_file: Archivo .mmd de entrada
        output_file: Archivo .png de salida
        use_npx: Si True, usa npx en lugar de mmdc global
    """
    print(f"{Colors.CYAN}Exportando diagrama a PNG...{Colors.RESET}")
    print(f"  Entrada: {input_file}")
    print(f"  Salida:  {output_file}\n")
    
    if use_npx:
        cmd = [
            'npx', '-p', '@mermaid-js/mermaid-cli', 'mmdc',
            '-i', str(input_file),
            '-o', str(output_file),
            '-b', 'transparent',
            '-t', 'default',
            '-w', '1920',
            '-H', '1080'
        ]
    else:
        cmd = [
            'mmdc',
            '-i', str(input_file),
            '-o', str(output_file),
            '-b', 'transparent',
            '-t', 'default',
            '-w', '1920',
            '-H', '1080'
        ]
    
    try:
        result = subprocess.run(cmd, 
                              capture_output=True, 
                              text=True, 
                              check=False)
        
        if result.returncode == 0:
            print(f"{Colors.GREEN}✓ Diagrama exportado exitosamente!{Colors.RESET}")
            print(f"{Colors.GREEN}  📄 {output_file}{Colors.RESET}")
            return True
        else:
            print(f"{Colors.RED}✗ Error al exportar:{Colors.RESET}")
            print(f"{Colors.YELLOW}{result.stderr}{Colors.RESET}")
            return False
            
    except FileNotFoundError:
        print(f"{Colors.RED}✗ Error: comando no encontrado{Colors.RESET}")
        return False
    except Exception as e:
        print(f"{Colors.RED}✗ Error inesperado: {e}{Colors.RESET}")
        return False

def main():
    print_header()
    
    # Rutas
    project_root = Path(__file__).parent.parent
    input_file = project_root / "design" / "database_erd.mmd"
    output_file = project_root / "design" / "database_erd.png"
    
    # Verificar que existe el archivo de entrada
    if not input_file.exists():
        print(f"{Colors.RED}✗ No se encontró {input_file}{Colors.RESET}")
        return 1
    
    print(f"📂 Archivo Mermaid: {input_file}")
    print(f"📂 Salida PNG: {output_file}\n")
    
    # Verificar si mmdc está instalado
    mmdc_installed = check_mmdc_installed()
    
    if not mmdc_installed:
        # Intentar con npx
        print(f"{Colors.CYAN}Intentando usar npx...{Colors.RESET}")
        success = export_to_png(input_file, output_file, use_npx=True)
        
        if not success:
            install_mmdc_instructions()
            return 1
    else:
        # Usar mmdc global
        success = export_to_png(input_file, output_file, use_npx=False)
        
        if not success:
            return 1
    
    # Resumen
    print()
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}¡Exportación completada!{Colors.RESET}")
    print()
    print(f"{Colors.GREEN}Ahora puedes:{Colors.RESET}")
    print(f"  • Abrir el archivo: {output_file}")
    print(f"  • Incluirlo en documentación")
    print(f"  • Compartirlo con tu equipo")
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
