#!/usr/bin/env python3
"""
Script para copiar imágenes de productos al directorio assets del dispositivo Android
Útil para actualizar imágenes durante desarrollo sin recompilar
"""

import subprocess
import sys
import os
from pathlib import Path

# Colores ANSI
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    WHITE = '\033[97m'
    RED = '\033[91m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header(text, color=Colors.CYAN):
    """Imprime un encabezado con estilo"""
    print(f"\n{color}{Colors.BOLD}{'='*60}")
    print(f"  {text}")
    print(f"{'='*60}{Colors.RESET}\n")

def run_command(command, description=""):
    """Ejecuta un comando y muestra el progreso"""
    try:
        if description:
            print(f"{Colors.YELLOW}➜ {description}...{Colors.RESET}", end=" ")
        
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            check=True
        )
        
        if description:
            print(f"{Colors.GREEN}✓{Colors.RESET}")
        
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        if description:
            print(f"{Colors.RED}✗{Colors.RESET}")
        print(f"{Colors.RED}Error: {e.stderr}{Colors.RESET}")
        return None

def check_adb_connection():
    """Verifica conexión ADB"""
    result = run_command("adb devices")
    if result and "device" in result and result.count('\n') > 1:
        return True
    return False

def get_product_images_dir():
    """Obtiene el directorio de imágenes de productos"""
    # Buscar desde el directorio del script
    script_dir = Path(__file__).parent.parent
    images_dir = script_dir / "product_images"
    
    if images_dir.exists():
        return images_dir
    
    # Alternativa: buscar en directorio actual
    images_dir = Path.cwd() / "product_images"
    if images_dir.exists():
        return images_dir
    
    return None

def copy_images_to_device(images_dir):
    """Copia las imágenes al dispositivo"""
    print_header("Copiando Imágenes de Productos", Colors.CYAN)
    
    # Obtener lista de directorios de productos
    product_dirs = [d for d in images_dir.iterdir() if d.is_dir() and d.name.startswith("product_")]
    
    if not product_dirs:
        print(f"{Colors.YELLOW}⚠ No se encontraron directorios de productos{Colors.RESET}")
        return False
    
    print(f"{Colors.WHITE}Encontrados {len(product_dirs)} directorios de productos{Colors.RESET}\n")
    
    # Ruta de destino en el dispositivo
    device_path = "/data/data/com.elitecouture.app/files/product_images"
    
    # Crear directorio en el dispositivo si no existe
    run_command(f'adb shell "mkdir -p {device_path}"', "Creando directorio en dispositivo")
    
    total_files = 0
    success_count = 0
    
    # Copiar cada directorio de producto
    for product_dir in sorted(product_dirs):
        product_name = product_dir.name
        print(f"\n{Colors.CYAN}📁 {product_name}{Colors.RESET}")
        
        # Obtener todas las imágenes en el directorio
        image_files = list(product_dir.glob("*.avif")) + \
                     list(product_dir.glob("*.webp")) + \
                     list(product_dir.glob("*.jpg")) + \
                     list(product_dir.glob("*.png"))
        
        if not image_files:
            print(f"  {Colors.YELLOW}⚠ Sin imágenes{Colors.RESET}")
            continue
        
        # Crear directorio del producto en el dispositivo
        device_product_path = f"{device_path}/{product_name}"
        run_command(f'adb shell "mkdir -p {device_product_path}"')
        
        # Copiar cada imagen
        for image_file in image_files:
            total_files += 1
            result = run_command(
                f'adb push "{image_file}" {device_product_path}/',
                f"  → {image_file.name}"
            )
            if result is not None:
                success_count += 1
    
    print(f"\n{Colors.GREEN}{'='*60}{Colors.RESET}")
    print(f"{Colors.BOLD}Resumen:{Colors.RESET}")
    print(f"  Total de archivos: {total_files}")
    print(f"  {Colors.GREEN}Exitosos: {success_count}{Colors.RESET}")
    if total_files - success_count > 0:
        print(f"  {Colors.RED}Fallidos: {total_files - success_count}{Colors.RESET}")
    
    return success_count == total_files

def set_permissions():
    """Establece permisos de lectura en el directorio"""
    device_path = "/data/data/com.elitecouture.app/files/product_images"
    
    print(f"\n{Colors.YELLOW}Configurando permisos...{Colors.RESET}")
    run_command(f'adb shell "chmod -R 755 {device_path}"')
    print(f"{Colors.GREEN}✓ Permisos configurados{Colors.RESET}")

def restart_app():
    """Reinicia la aplicación para que cargue las nuevas imágenes"""
    print(f"\n{Colors.YELLOW}Reiniciando aplicación...{Colors.RESET}")
    
    # Detener app
    run_command("adb shell am force-stop com.elitecouture.app")
    
    # Iniciar app
    result = run_command("adb shell am start -n com.elitecouture.app/.ui.MainActivity")
    
    if result is not None:
        print(f"{Colors.GREEN}✓ Aplicación reiniciada{Colors.RESET}")
    else:
        print(f"{Colors.RED}✗ Error reiniciando aplicación{Colors.RESET}")

def main():
    """Función principal"""
    print_header("Copy Product Images - Elite Couture", Colors.CYAN)
    
    # Verificar conexión ADB
    if not check_adb_connection():
        print(f"{Colors.RED}❌ No se detectó ningún dispositivo conectado{Colors.RESET}")
        print(f"\n{Colors.YELLOW}Asegúrate de que:")
        print(f"  - El emulador/dispositivo está encendido")
        print(f"  - ADB está instalado y en el PATH")
        print(f"  - USB debugging está habilitado{Colors.RESET}")
        sys.exit(1)
    
    print(f"{Colors.GREEN}✓ Dispositivo conectado{Colors.RESET}")
    
    # Obtener directorio de imágenes
    images_dir = get_product_images_dir()
    
    if not images_dir:
        print(f"\n{Colors.RED}❌ No se encontró el directorio 'product_images'{Colors.RESET}")
        print(f"{Colors.YELLOW}Asegúrate de ejecutar el script desde el directorio raíz del proyecto{Colors.RESET}")
        sys.exit(1)
    
    print(f"{Colors.GREEN}✓ Directorio de imágenes: {images_dir}{Colors.RESET}")
    
    # Copiar imágenes
    success = copy_images_to_device(images_dir)
    
    if not success:
        print(f"\n{Colors.YELLOW}⚠ Algunas imágenes no se copiaron correctamente{Colors.RESET}")
        return
    
    # Configurar permisos
    set_permissions()
    
    # Preguntar si reiniciar app
    print(f"\n{Colors.CYAN}¿Deseas reiniciar la aplicación? (s/n): {Colors.RESET}", end="")
    try:
        response = input().strip().lower()
        if response in ['s', 'y', 'yes', 'si', 'sí']:
            restart_app()
    except (KeyboardInterrupt, EOFError):
        print(f"\n{Colors.YELLOW}Reinicio cancelado{Colors.RESET}")
    
    print(f"\n{Colors.GREEN}✓ Proceso completado{Colors.RESET}\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n\n{Colors.YELLOW}Script interrumpido por el usuario{Colors.RESET}")
        sys.exit(0)
    except Exception as e:
        print(f"\n{Colors.RED}Error inesperado: {e}{Colors.RESET}")
        sys.exit(1)
