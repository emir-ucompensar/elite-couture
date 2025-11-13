#!/usr/bin/env python3
"""
Elite Couture - Source Code Packager
Comprime solo el código fuente esencial, excluyendo builds, releases y archivos privados
"""

import os
import zipfile
from pathlib import Path
from datetime import datetime

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
    print(f"{Colors.CYAN}{'=' * 60}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}Elite Couture - Source Code Packager{Colors.RESET}")
    print(f"{Colors.CYAN}{'=' * 60}{Colors.RESET}\n")

# Directorios y archivos a EXCLUIR
EXCLUDE_DIRS = {
    # Build outputs
    'build',
    '.gradle',
    'gradle',
    
    # IDE
    '.idea',
    '.vscode',
    
    # Version control
    '.git',
    '.github',
    
    # Releases y archivos compilados
    'releases',
    '*.apk',
    '*.aab',
    
    # Imágenes de productos (muy pesadas)
    'product_images',
    
    # Cache y temporales
    '__pycache__',
    '*.pyc',
    '.DS_Store',
    'Thumbs.db',
    
    # Archivos locales
    'local.properties',
    
    # Testing outputs
    '.pytest_cache',
    
    # Excluir .venv correctamente
    '.venv',
    
    # Archivos de release
    '*.zip',
    'RELEASE_INSTRUCTIONS.md',
}

# Extensiones de archivo a INCLUIR (whitelist)
INCLUDE_EXTENSIONS = {
    # Código fuente
    '.kt', '.java', '.xml', '.gradle', '.kts',
    
    # Configuración
    '.json', '.properties', '.pro', '.toml',
    
    # Recursos
    '.png', '.jpg', '.jpeg', '.webp', '.avif', '.svg',
    '.ttf', '.otf',  # Fuentes
    
    # Documentación
    '.md', '.txt',
    
    # Scripts
    '.py', '.sh', '.bat',
    
    # Otros
    '.gitignore', '.gitattributes',
}

# Archivos específicos a INCLUIR (siempre)
ALWAYS_INCLUDE = {
    'README.md',
    'LICENSE',
    'settings.gradle',
    'build.gradle',
    'gradle.properties',
    'gradlew',
    'gradlew.bat',
}

def should_exclude(path: Path, project_root: Path) -> bool:
    """
    Determina si un archivo o directorio debe ser excluido
    """
    relative_path = str(path.relative_to(project_root))

    # Incluir explícitamente cualquier cosa bajo 'design/'
    try:
        if 'design' in path.relative_to(project_root).parts:
            return False
    except ValueError:
        pass

    # Verificar si es un directorio excluido
    for part in path.parts:
        if part in EXCLUDE_DIRS:
            return True

    # Verificar patrones de exclusión
    for pattern in EXCLUDE_DIRS:
        if pattern.startswith('*'):
            if relative_path.endswith(pattern[1:]):
                return True

    # Si es archivo, verificar extensión
    if path.is_file():
        # Archivos que siempre se incluyen
        if path.name in ALWAYS_INCLUDE:
            return False

        # Verificar extensión
        if path.suffix not in INCLUDE_EXTENSIONS and path.suffix != '':
            return True

        # Excluir archivos muy grandes (>10MB)
        if path.stat().st_size > 10 * 1024 * 1024:
            return True

    return False

def get_directory_size(path: Path) -> int:
    """Calcula el tamaño total de un directorio"""
    total_size = 0
    try:
        for item in path.rglob('*'):
            if item.is_file():
                total_size += item.stat().st_size
    except (PermissionError, OSError):
        pass
    return total_size

def format_size(bytes_size: int) -> str:
    """Formatea bytes a formato legible"""
    for unit in ['B', 'KB', 'MB', 'GB']:
        if bytes_size < 1024.0:
            return f"{bytes_size:.2f} {unit}"
        bytes_size /= 1024.0
    return f"{bytes_size:.2f} TB"

def create_source_package(project_root: Path, output_name: str = None) -> Path:
    """
    Crea un archivo ZIP con el código fuente esencial
    """
    # Crear directorio releases/pending_review si no existe
    output_dir = project_root / 'releases' / 'pending_review'
    output_dir.mkdir(parents=True, exist_ok=True)
    
    # Generar nombre de salida
    if output_name is None:
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        output_name = f"elite-couture-source_{timestamp}.zip"
    
    output_path = output_dir / output_name
    
    print(f"{Colors.CYAN}Empaquetando código fuente...{Colors.RESET}\n")
    
    # Estadísticas
    files_included = 0
    files_excluded = 0
    total_size = 0
    
    with zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for item in project_root.rglob('*'):
            # Saltar el propio archivo ZIP
            if item == output_path:
                continue
            
            # Verificar si debe excluirse
            if should_exclude(item, project_root):
                files_excluded += 1
                continue
            
            if item.is_file():
                try:
                    # Calcular ruta relativa
                    arcname = item.relative_to(project_root)
                    
                    # Agregar al ZIP
                    zipf.write(item, arcname)
                    
                    files_included += 1
                    file_size = item.stat().st_size
                    total_size += file_size
                    
                    # Mostrar progreso cada 50 archivos
                    if files_included % 50 == 0:
                        print(f"  {Colors.GREEN}✓{Colors.RESET} {files_included} archivos procesados...", end='\r')
                
                except (PermissionError, OSError) as e:
                    print(f"{Colors.YELLOW}⚠ Advertencia: No se pudo acceder a {item.name}: {e}{Colors.RESET}")
                    files_excluded += 1
    
    print(f"\n{Colors.GREEN}✓ Empaquetado completado!{Colors.RESET}\n")
    
    return output_path, files_included, files_excluded, total_size

def main():
    print_header()
    
    # Detectar raíz del proyecto como el directorio actual desde donde se ejecuta el script
    project_root = Path.cwd()
    
    print(f"Proyecto: {project_root}")
    print(f"Calculando tamaño del proyecto...\n")
    
    # Crear paquete
    output_path, included, excluded, size = create_source_package(project_root)
    
    # Mostrar estadísticas
    print(f"{Colors.CYAN}{'─' * 60}{Colors.RESET}")
    print(f"{Colors.BOLD}Estadísticas del Empaquetado:{Colors.RESET}\n")
    print(f"Archivos incluidos:  {Colors.GREEN}{included}{Colors.RESET}")
    print(f"Archivos excluidos:  {Colors.YELLOW}{excluded}{Colors.RESET}")
    print(f"Tamaño del ZIP:      {Colors.CYAN}{format_size(output_path.stat().st_size)}{Colors.RESET}")
    print(f"Tamaño original:     {Colors.MAGENTA}{format_size(size)}{Colors.RESET}")
    print(f"Compresión:          {Colors.GREEN}{(1 - output_path.stat().st_size / size) * 100:.1f}%{Colors.RESET}")
    print(f"\nArchivo generado:    {Colors.BOLD}{output_path.name}{Colors.RESET}")
    print(f"{Colors.CYAN}{'─' * 60}{Colors.RESET}\n")
    
    # Listar algunos directorios excluidos importantes
    print(f"{Colors.YELLOW}Directorios excluidos (no esenciales):{Colors.RESET}")
    excluded_info = [
        "  • build/ - Archivos compilados",
        "  • .gradle/ - Cache de Gradle",
        "  • .github/ - Workflows de GitHub",
        "  • product_images/ - Imágenes fuente (muy pesadas)",
        "  • .git/ - Historial de Git",
        "  • *.apk - APKs compilados",
    ]
    for info in excluded_info:
        print(info)
    
    print(f"\n{Colors.GREEN}¡Código fuente empaquetado exitosamente!{Colors.RESET}")
    print(f"{Colors.CYAN}Ubicación: releases/pending_review/{output_path.name}{Colors.RESET}")
    print(f"{Colors.CYAN}Listo para compartir o subir a GitHub Releases{Colors.RESET}\n")

if __name__ == "__main__":
    import sys
    sys.exit(main() or 0)
