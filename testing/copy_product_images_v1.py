#!/usr/bin/env python3
"""
Elite Couture - Product Image Processor
Convierte imágenes AVIF a WebP y las copia a assets para la app Android
"""

import os
import sys
import shutil
from pathlib import Path
from typing import List, Tuple

# Colores ANSI para terminal
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    MAGENTA = '\033[95m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header():
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}Elite Couture - Image Processor{Colors.RESET}")
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}\n")

def convert_avif_to_webp(input_path: Path, output_path: Path) -> bool:
    """
    Convierte AVIF a WebP usando Pillow.
    Si Pillow no está disponible, copia directamente.
    """
    try:
        from PIL import Image
        
        # Abrir imagen AVIF
        with Image.open(input_path) as img:
            # Convertir a RGB si es necesario (AVIF puede tener alfa)
            if img.mode in ('RGBA', 'LA', 'P'):
                background = Image.new('RGB', img.size, (255, 255, 255))
                if img.mode == 'P':
                    img = img.convert('RGBA')
                background.paste(img, mask=img.split()[-1] if img.mode == 'RGBA' else None)
                img = background
            
            # Guardar como WebP con calidad alta
            img.save(output_path, 'WEBP', quality=90, method=6)
        
        return True
    
    except ImportError:
        print(f"{Colors.YELLOW}⚠ Pillow no instalado. Instalando...{Colors.RESET}")
        os.system(f"{sys.executable} -m pip install Pillow --quiet")
        
        # Reintentar
        try:
            from PIL import Image
            with Image.open(input_path) as img:
                if img.mode in ('RGBA', 'LA', 'P'):
                    background = Image.new('RGB', img.size, (255, 255, 255))
                    if img.mode == 'P':
                        img = img.convert('RGBA')
                    background.paste(img, mask=img.split()[-1] if img.mode == 'RGBA' else None)
                    img = background
                img.save(output_path, 'WEBP', quality=90, method=6)
            return True
        except Exception as e:
            print(f"{Colors.RED}✗ Error instalando Pillow: {e}{Colors.RESET}")
            return False
    
    except Exception as e:
        print(f"{Colors.RED}✗ Error convirtiendo {input_path.name}: {e}{Colors.RESET}")
        return False

def process_product_images(source_root: Path, dest_root: Path, start_from: int = 1, max_products: int = None) -> Tuple[int, int, List[str]]:
    """
    Procesa todas las imágenes de productos.
    
    Args:
        source_root: Carpeta origen con las imágenes
        dest_root: Carpeta destino para las imágenes procesadas
        start_from: Número de producto desde el cual comenzar (default: 1)
        max_products: Número máximo de productos a procesar (default: None = todos los disponibles)
    
    Returns:
        (total_copied, errors, warnings)
    """
    total_copied = 0
    errors = 0
    warnings = []
    
    # Crear directorio de destino si no existe
    dest_root.mkdir(parents=True, exist_ok=True)
    
    # Detectar automáticamente cuántas carpetas de productos existen
    product_folders = sorted([f for f in source_root.iterdir() if f.is_dir() and f.name.startswith("product_")])
    
    if not product_folders:
        print(f"{Colors.RED}ERROR: No se encontraron carpetas de productos en {source_root}{Colors.RESET}")
        return 0, 1, ["No hay carpetas product_* en el directorio"]
    
    # Extraer números de productos disponibles
    available_products = []
    for folder in product_folders:
        try:
            num = int(folder.name.split("_")[1])
            available_products.append(num)
        except (IndexError, ValueError):
            continue
    
    available_products.sort()
    
    if not available_products:
        print(f"{Colors.RED}ERROR: No se pudieron extraer números de productos{Colors.RESET}")
        return 0, 1, ["No se encontraron números válidos en las carpetas"]
    
    print(f"{Colors.CYAN}Productos disponibles: {min(available_products)} - {max(available_products)}{Colors.RESET}")
    print(f"{Colors.CYAN}Procesando desde: {start_from}{Colors.RESET}")
    
    # Filtrar productos según start_from y max_products
    products_to_process = [p for p in available_products if p >= start_from]
    if max_products:
        products_to_process = products_to_process[:max_products]
    
    total_expected = len(products_to_process) * 3
    print(f"{Colors.CYAN}Total de productos a procesar: {len(products_to_process)} ({total_expected} imágenes esperadas){Colors.RESET}")
    print()
    
    for i in products_to_process:
        product_num = f"{i:02d}"
        product_folder = source_root / f"product_{product_num}"
        
        print(f"{Colors.YELLOW}📦 Procesando Producto {product_num}...{Colors.RESET}")
        
        if not product_folder.exists():
            error_msg = f"No existe la carpeta {product_folder}"
            print(f"  {Colors.RED}✗ {error_msg}{Colors.RESET}")
            warnings.append(error_msg)
            errors += 1
            continue
        
        # Buscar imágenes (soportar múltiples formatos)
        image_extensions = ['.jpg', '.jpeg', '.png', '.avif', '.webp']
        image_files = []
        
        for ext in image_extensions:
            image_files.extend(product_folder.glob(f"*{ext}"))
        
        if not image_files:
            error_msg = f"No se encontraron imágenes en {product_folder.name}"
            print(f"  {Colors.RED}✗ {error_msg}{Colors.RESET}")
            warnings.append(error_msg)
            errors += 1
            continue
        
        # Ordenar y tomar solo las primeras 3
        image_files = sorted(image_files, key=lambda x: x.name)[:3]
        
        # Advertir si hay menos de 3 imágenes
        if len(image_files) < 3:
            warning_msg = f"Producto {product_num}: solo {len(image_files)} imágenes (se esperaban 3)"
            warnings.append(warning_msg)
            print(f"  {Colors.YELLOW}⚠ {warning_msg}{Colors.RESET}")
        
        # Procesar cada imagen
        for img_num, image_file in enumerate(image_files, start=1):
            # Determinar extensión de salida
            if image_file.suffix.lower() == '.avif':
                # Convertir AVIF a WebP
                output_ext = 'webp'
                dest_file = dest_root / f"product_{product_num}_img_{img_num}.{output_ext}"
                
                success = convert_avif_to_webp(image_file, dest_file)
                
                if success:
                    file_size_kb = dest_file.stat().st_size / 1024
                    print(f"  {Colors.GREEN}✓ Convertido: {image_file.name} → product_{product_num}_img_{img_num}.{output_ext} ({file_size_kb:.1f} KB){Colors.RESET}")
                    total_copied += 1
                else:
                    errors += 1
            else:
                # Copiar directamente si no es AVIF
                output_ext = image_file.suffix.lstrip('.')
                dest_file = dest_root / f"product_{product_num}_img_{img_num}.{output_ext}"
                
                try:
                    shutil.copy2(image_file, dest_file)
                    file_size_kb = dest_file.stat().st_size / 1024
                    print(f"  {Colors.GREEN}✓ Copiado: {image_file.name} → product_{product_num}_img_{img_num}.{output_ext} ({file_size_kb:.1f} KB){Colors.RESET}")
                    total_copied += 1
                except Exception as e:
                    print(f"  {Colors.RED}✗ Error copiando {image_file.name}: {e}{Colors.RESET}")
                    errors += 1
    
    return total_copied, errors, warnings

def main():
    print_header()
    
    # Configuración de rutas
    source_root = Path("product_images")
    dest_root = Path("app/src/main/assets/images")
    
    # Verificar que existe la carpeta fuente
    if not source_root.exists():
        print(f"{Colors.RED}ERROR: No se encontró la carpeta {source_root}{Colors.RESET}")
        return 1
    
    print(f"📂 Origen: {source_root}")
    print(f"📂 Destino: {dest_root}")
    print()
    
    # Parámetros opcionales desde argumentos
    start_from = 1
    max_products = None
    
    if len(sys.argv) > 1:
        try:
            start_from = int(sys.argv[1])
            print(f"{Colors.YELLOW}Iniciando desde producto: {start_from}{Colors.RESET}")
        except ValueError:
            print(f"{Colors.RED}ERROR: El primer argumento debe ser un número{Colors.RESET}")
            return 1
    
    if len(sys.argv) > 2:
        try:
            max_products = int(sys.argv[2])
            print(f"{Colors.YELLOW}Máximo de productos a procesar: {max_products}{Colors.RESET}")
        except ValueError:
            print(f"{Colors.RED}ERROR: El segundo argumento debe ser un número{Colors.RESET}")
            return 1
    
    print()
    
    # Procesar imágenes
    total_copied, errors, warnings = process_product_images(source_root, dest_root, start_from, max_products)
    
    # Resumen
    print()
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}Resumen:{Colors.RESET}")
    print(f"  {Colors.GREEN if errors == 0 else Colors.YELLOW}Imágenes procesadas: {total_copied}{Colors.RESET}")
    print(f"  {Colors.GREEN if errors == 0 else Colors.RED}Errores: {errors}{Colors.RESET}")
    
    if warnings:
        print(f"\n{Colors.YELLOW}Advertencias:{Colors.RESET}")
        for warning in warnings:
            print(f"  ⚠ {warning}")
    
    print(f"{Colors.CYAN}{'=' * 50}{Colors.RESET}")
    
    # Mensaje final
    if total_copied > 0 and errors == 0:
        print(f"\n{Colors.GREEN}{Colors.BOLD}✓ ¡Todas las imágenes procesadas exitosamente!{Colors.RESET}")
        print(f"{Colors.CYAN}Ahora puedes compilar la app con: gradle assembleDebug{Colors.RESET}")
    elif total_copied > 0:
        print(f"\n{Colors.YELLOW}! Algunas imágenes no se procesaron. Revisa los errores arriba.{Colors.RESET}")
    else:
        print(f"\n{Colors.RED}✗ No se procesó ninguna imagen. Verifica las carpetas y archivos.{Colors.RESET}")
        return 1
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
