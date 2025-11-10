"""
Script de Migración de Imágenes a Supabase Storage
Elite Couture - Subida masiva de imágenes de productos

Este script:
1. Escanea todas las carpetas de productos (product_01 a product_25)
2. Convierte imágenes .avif a .jpg si es necesario
3. Sube todas las imágenes al bucket de Supabase
4. Organiza las imágenes en rutas descriptivas
5. Genera un archivo JSON con las URLs públicas

Requisitos:
    pip install supabase Pillow pillow-avif-plugin python-dotenv

Uso:
    python upload_all_product_images.py
"""

import os
import sys
from pathlib import Path
from datetime import datetime
from io import BytesIO
from PIL import Image
import json
import time

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: Librería 'supabase' no instalada")
    print("   Instala con: pip install supabase")
    sys.exit(1)

try:
    from PIL import Image
    # Intentar importar soporte AVIF
    try:
        import pillow_avif
    except ImportError:
        print("Warning: pillow-avif-plugin no instalado")
        print("   Para soportar .avif: pip install pillow-avif-plugin")
except ImportError:
    print("Error: Librería 'Pillow' no instalada")
    print("   Instala con: pip install Pillow")
    sys.exit(1)

try:
    from dotenv import load_dotenv
except ImportError:
    print("Error: Librería 'python-dotenv' no instalada")
    print("   Instala con: pip install python-dotenv")
    sys.exit(1)


# =============================================================================
# CONFIGURACIÓN
# =============================================================================

# Cargar variables de entorno
env_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(env_path)

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
    print(f"{Colors.GREEN}{Colors.RESET} {text}")

def print_error(text):
    print(f"{Colors.RED}{Colors.RESET} {text}")

def print_warning(text):
    print(f"{Colors.YELLOW}{Colors.RESET} {text}")

def print_info(text):
    print(f"{Colors.CYAN}{Colors.RESET} {text}")

def print_step(text):
    print(f"{Colors.BLUE}{Colors.RESET} {text}")

# Credenciales de Supabase
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print_error("Faltan credenciales de Supabase en .env")
    print_info("Asegúrate de tener SUPABASE_URL y SUPABASE_ANON_KEY")
    sys.exit(1)

BUCKET_NAME = "product-images"

# Directorio de imágenes de productos
SCRIPT_DIR = Path(__file__).parent
PROJECT_ROOT = SCRIPT_DIR.parent.parent
IMAGES_DIR = PROJECT_ROOT / "design" / "product_images"

# Archivo de salida con las URLs
OUTPUT_JSON = SCRIPT_DIR / "uploaded_images_urls.json"

# =============================================================================
# FUNCIONES DE CONVERSIÓN
# =============================================================================

def convert_avif_to_jpg(avif_path: Path) -> BytesIO:
    """Convierte una imagen AVIF a JPG en memoria"""
    try:
        img = Image.open(avif_path)
        # Convertir a RGB si es necesario (AVIF puede tener canal alpha)
        if img.mode in ('RGBA', 'LA', 'P'):
            # Crear fondo blanco
            background = Image.new('RGB', img.size, (255, 255, 255))
            if img.mode == 'P':
                img = img.convert('RGBA')
            background.paste(img, mask=img.split()[-1] if img.mode in ('RGBA', 'LA') else None)
            img = background
        elif img.mode != 'RGB':
            img = img.convert('RGB')
        
        # Guardar como JPG en memoria
        buffer = BytesIO()
        img.save(buffer, format='JPEG', quality=85, optimize=True)
        buffer.seek(0)
        return buffer
    except Exception as e:
        raise Exception(f"Error convirtiendo imagen: {str(e)}")

def optimize_image(image_path: Path, max_size_kb: int = 500) -> BytesIO:
    """Optimiza una imagen y la devuelve como buffer"""
    try:
        img = Image.open(image_path)
        
        # Convertir a RGB si es necesario
        if img.mode in ('RGBA', 'LA', 'P'):
            background = Image.new('RGB', img.size, (255, 255, 255))
            if img.mode == 'P':
                img = img.convert('RGBA')
            background.paste(img, mask=img.split()[-1] if img.mode in ('RGBA', 'LA') else None)
            img = background
        elif img.mode != 'RGB':
            img = img.convert('RGB')
        
        # Redimensionar si es muy grande (mantener aspecto)
        max_dimension = 1920
        if max(img.size) > max_dimension:
            img.thumbnail((max_dimension, max_dimension), Image.Resampling.LANCZOS)
        
        # Guardar con compresión progresiva
        buffer = BytesIO()
        quality = 85
        img.save(buffer, format='JPEG', quality=quality, optimize=True, progressive=True)
        
        # Reducir calidad si excede el tamaño máximo
        while buffer.tell() > max_size_kb * 1024 and quality > 60:
            buffer = BytesIO()
            quality -= 5
            img.save(buffer, format='JPEG', quality=quality, optimize=True, progressive=True)
        
        buffer.seek(0)
        return buffer
    except Exception as e:
        raise Exception(f"Error optimizando imagen: {str(e)}")

# =============================================================================
# FUNCIONES DE SUBIDA
# =============================================================================

def upload_image_to_supabase(supabase: Client, image_buffer: BytesIO, 
                             remote_path: str) -> str:
    """Sube una imagen al bucket de Supabase"""
    try:
        # Subir archivo
        supabase.storage.from_(BUCKET_NAME).upload(
            path=remote_path,
            file=image_buffer.getvalue(),
            file_options={"content-type": "image/jpeg", "upsert": "true"}
        )
        
        # Obtener URL pública
        url = supabase.storage.from_(BUCKET_NAME).get_public_url(remote_path)
        return url
    except Exception as e:
        raise Exception(f"Error subiendo a Supabase: {str(e)}")

def scan_product_folders() -> dict:
    """Escanea todas las carpetas de productos y sus imágenes"""
    products = {}
    
    for i in range(1, 26):  # product_01 a product_25
        folder_name = f"product_{i:02d}"
        folder_path = IMAGES_DIR / folder_name
        
        if folder_path.exists() and folder_path.is_dir():
            images = []
            # Buscar archivos de imagen
            for ext in ['.avif', '.jpg', '.jpeg', '.png', '.webp']:
                images.extend(folder_path.glob(f"*{ext}"))
            
            if images:
                products[folder_name] = {
                    'path': folder_path,
                    'images': sorted(images)  # Ordenar alfabéticamente
                }
    
    return products

# =============================================================================
# SCRIPT PRINCIPAL
# =============================================================================

def main():
    print("=" * 80)
    print("  MIGRACIÓN DE IMÁGENES A SUPABASE STORAGE")
    print("  Elite Couture - Upload masivo de productos")
    print("=" * 80)
    print()
    
    # Verificar directorio de imágenes
    if not IMAGES_DIR.exists():
        print_error(f"No se encontró el directorio de imágenes: {IMAGES_DIR}")
        sys.exit(1)
    
    print_info(f"Directorio de imágenes: {IMAGES_DIR}")
    print()
    
    # Escanear productos
    print_step("Escaneando carpetas de productos...")
    products = scan_product_folders()
    
    if not products:
        print_error("No se encontraron productos con imágenes")
        sys.exit(1)
    
    total_products = len(products)
    total_images = sum(len(p['images']) for p in products.values())
    
    print_success(f"Encontrados {total_products} productos con {total_images} imágenes")
    print()
    
    # Conectar a Supabase
    print_step("Conectando a Supabase...")
    try:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
        print_success("Conectado a Supabase")
    except Exception as e:
        print_error(f"Error conectando a Supabase: {str(e)}")
        sys.exit(1)
    
    print()
    
    # Confirmar antes de proceder
    print(f"{Colors.YELLOW}{Colors.BOLD}¿Deseas continuar con la subida de {total_images} imágenes?{Colors.RESET}")
    response = input("Escribe 'SI' para continuar: ").strip().upper()
    
    if response != 'SI':
        print_warning("Operación cancelada por el usuario")
        sys.exit(0)
    
    print()
    print("=" * 80)
    print("  INICIANDO SUBIDA DE IMÁGENES")
    print("=" * 80)
    print()
    
    # Resultados
    uploaded_urls = {}
    upload_stats = {
        'success': 0,
        'failed': 0,
        'total_size': 0
    }
    
    start_time = time.time()
    
    # Procesar cada producto
    for product_num, (product_name, product_data) in enumerate(products.items(), 1):
        print(f"{Colors.CYAN}[{product_num}/{total_products}]{Colors.RESET} Procesando {Colors.BOLD}{product_name}{Colors.RESET}...")
        
        product_urls = []
        
        # Procesar cada imagen del producto
        for img_idx, image_path in enumerate(product_data['images'], 1):
            image_name = image_path.name
            file_ext = image_path.suffix.lower()
            
            try:
                # Generar nombre de archivo remoto
                # Formato: products/product_01/image_1.jpg
                remote_name = f"image_{img_idx}.jpg"
                remote_path = f"products/{product_name}/{remote_name}"
                
                print(f"  [{img_idx}/{len(product_data['images'])}] {image_name} → {remote_path}", end=" ")
                
                # Procesar imagen
                if file_ext == '.avif':
                    image_buffer = convert_avif_to_jpg(image_path)
                else:
                    image_buffer = optimize_image(image_path)
                
                # Obtener tamaño
                size_kb = len(image_buffer.getvalue()) / 1024
                
                # Subir a Supabase
                public_url = upload_image_to_supabase(supabase, image_buffer, remote_path)
                
                product_urls.append({
                    'original_name': image_name,
                    'remote_path': remote_path,
                    'url': public_url,
                    'size_kb': round(size_kb, 2)
                })
                
                upload_stats['success'] += 1
                upload_stats['total_size'] += size_kb
                
                print(f"{Colors.GREEN}✓{Colors.RESET} ({size_kb:.1f} KB)")
                
            except Exception as e:
                print(f"{Colors.RED}✗{Colors.RESET} Error: {str(e)}")
                upload_stats['failed'] += 1
        
        uploaded_urls[product_name] = product_urls
        print()
    
    end_time = time.time()
    duration = end_time - start_time
    
    # Guardar resultados en JSON
    print_step("Guardando URLs en archivo JSON...")
    output_data = {
        'timestamp': datetime.now().isoformat(),
        'bucket': BUCKET_NAME,
        'stats': {
            'total_products': total_products,
            'total_images': total_images,
            'uploaded_successfully': upload_stats['success'],
            'failed': upload_stats['failed'],
            'total_size_mb': round(upload_stats['total_size'] / 1024, 2),
            'duration_seconds': round(duration, 2)
        },
        'products': uploaded_urls
    }
    
    with open(OUTPUT_JSON, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, indent=2, ensure_ascii=False)
    
    print_success(f"URLs guardadas en: {OUTPUT_JSON}")
    print()
    
    # Resumen final
    print("=" * 80)
    print("  RESUMEN DE MIGRACIÓN")
    print("=" * 80)
    print()
    print(f"  Productos procesados:    {total_products}")
    print(f"  Imágenes exitosas:       {Colors.GREEN}{upload_stats['success']}{Colors.RESET}")
    print(f"  Imágenes fallidas:       {Colors.RED if upload_stats['failed'] > 0 else Colors.GREEN}{upload_stats['failed']}{Colors.RESET}")
    print(f"  Tamaño total:            {upload_stats['total_size'] / 1024:.2f} MB")
    print(f"  Tiempo transcurrido:     {duration:.2f} segundos")
    print()
    
    if upload_stats['failed'] == 0:
        print(f"{Colors.GREEN}{Colors.BOLD}TODAS LAS IMÁGENES SE SUBIERON EXITOSAMENTE{Colors.RESET}")
    else:
        print(f"{Colors.YELLOW}Se completó con {upload_stats['failed']} errores{Colors.RESET}")
    
    print()
    print("=" * 80)

if __name__ == "__main__":
    main()
