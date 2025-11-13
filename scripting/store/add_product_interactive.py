"""
Script Interactivo para Añadir Productos a Supabase
Elite Couture - Creación de productos uno por uno

Este script permite:
- Ingresar información del producto de forma interactiva
- Seleccionar un directorio de imágenes existente (product_X)
- Validar que las imágenes existen en el bucket
- Corregir errores sin perder datos
- Añadir múltiples productos en una sesión

Requisitos:
    pip install supabase python-dotenv

Uso:
    python add_product_interactive.py
"""

import os
import sys
import json
from pathlib import Path
from datetime import datetime
import uuid as uuid_lib

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: Librería 'supabase' no instalada")
    print("   Instala con: pip install supabase")
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

# Ruta local de las imágenes de productos
SCRIPT_DIR = Path(__file__).parent
LOCAL_IMAGES_DIR = SCRIPT_DIR.parent.parent / "design" / "product_images"

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
    print(f"{Colors.BLUE}▶{Colors.RESET} {text}")

def print_header(text):
    print(f"\n{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{text.center(80)}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}\n")

# Credenciales de Supabase
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")
SUPABASE_BUCKET = "product-images"

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print_error("Faltan credenciales de Supabase en .env")
    print_info("Asegúrate de tener SUPABASE_URL y SUPABASE_ANON_KEY")
    sys.exit(1)

# =============================================================================
# UTILIDADES
# =============================================================================

def clear_screen():
    """Limpia la pantalla (opcional)"""
    os.system('cls' if os.name == 'nt' else 'clear')

def get_input(prompt, default=None, allow_empty=False):
    """Solicita input con valor por defecto"""
    if default:
        full_prompt = f"{prompt} [{Colors.CYAN}{default}{Colors.RESET}]: "
    else:
        full_prompt = f"{prompt}: "
    
    value = input(full_prompt).strip()
    
    if not value and default:
        return default
    
    if not value and not allow_empty:
        print_warning("Este campo no puede estar vacío")
        return get_input(prompt, default, allow_empty)
    
    return value

def get_number(prompt, default=None, min_value=None, max_value=None):
    """Solicita un número con validación"""
    while True:
        value_str = get_input(prompt, str(default) if default else None)
        
        try:
            value = float(value_str)
            
            if min_value is not None and value < min_value:
                print_error(f"El valor debe ser al menos {min_value}")
                continue
            
            if max_value is not None and value > max_value:
                print_error(f"El valor debe ser como máximo {max_value}")
                continue
            
            return value
        except ValueError:
            print_error("Por favor ingresa un número válido")

def get_choice(prompt, options):
    """Solicita elegir una opción de una lista"""
    print(f"\n{prompt}")
    for i, option in enumerate(options, 1):
        print(f"  {i}. {option}")
    
    while True:
        try:
            choice = int(input(f"\nSelecciona una opción (1-{len(options)}): "))
            if 1 <= choice <= len(options):
                return options[choice - 1]
            else:
                print_error(f"Por favor elige un número entre 1 y {len(options)}")
        except ValueError:
            print_error("Por favor ingresa un número válido")

def confirm(prompt):
    """Solicita confirmación sí/no"""
    response = input(f"{prompt} (s/n): ").strip().lower()
    return response in ['s', 'si', 'sí', 'y', 'yes']

# =============================================================================
# FUNCIONES DE SUPABASE
# =============================================================================

def list_available_product_folders_local():
    """Lista las carpetas product_X disponibles localmente"""
    try:
        if not LOCAL_IMAGES_DIR.exists():
            print_error(f"No se encuentra el directorio: {LOCAL_IMAGES_DIR}")
            return []
        
        folders = []
        for item in LOCAL_IMAGES_DIR.iterdir():
            if item.is_dir() and item.name.startswith('product_'):
                # Verificar que tenga imágenes
                images = list(item.glob('*.avif')) + list(item.glob('*.jpg')) + \
                        list(item.glob('*.jpeg')) + list(item.glob('*.png')) + \
                        list(item.glob('*.webp'))
                if images:
                    folders.append(item.name)
        
        return sorted(folders)
    
    except Exception as e:
        print_error(f"Error listando carpetas locales: {str(e)}")
        return []

def get_images_from_folder(supabase: Client, folder_name):
    """Obtiene las URLs de las imágenes de una carpeta específica"""
    try:
        # La estructura en el bucket es: products/product_01/image_1.jpg
        bucket_path = f"products/{folder_name}"
        
        # Listar archivos en la carpeta
        files = supabase.storage.from_(SUPABASE_BUCKET).list(bucket_path)
        
        if not files:
            return []
        
        # Construir URLs públicas
        image_urls = []
        for file in files:
            if file['name'].lower().endswith(('.webp', '.jpg', '.jpeg', '.png', '.avif')):
                # URL pública del archivo
                public_url = supabase.storage.from_(SUPABASE_BUCKET).get_public_url(
                    f"{bucket_path}/{file['name']}"
                )
                image_urls.append(public_url)
        
        return image_urls
    
    except Exception as e:
        print_error(f"Error obteniendo imágenes: {str(e)}")
        return []

def create_product(supabase: Client, product_data):
    """Crea un producto en Supabase"""
    try:
        result = supabase.table('products').insert(product_data).execute()
        return True, result.data[0] if result.data else None
    except Exception as e:
        return False, str(e)

# =============================================================================
# FLUJO PRINCIPAL
# =============================================================================

def select_tags(gender, product_type):
    """Permite seleccionar tags de una lista predefinida por números"""
    
    # Lista completa de tags disponibles
    available_tags = [
        # Géneros (siempre se incluyen automáticamente)
        "Hombre", "Mujer", "Unisex",
        
        # Categorías principales
        "Camisas", "Pantalones", "Chaquetas", "Vestidos", "Faldas",
        "Blusas", "Tops", "Suéteres", "Abrigos", "Shorts",
        "Zapatos", "Accesorios", "Bolsos", "Conjuntos", "Monos",
        "Bodys", "Básicos",
        
        # Estilos
        "Casual", "Formal", "Deportivo", "Elegante", "Moderno",
        "Clásico", "Vintage", "Bohemio", "Minimalista", "Urbano",
        
        # Ocasiones
        "Oficina", "Fiesta", "Playa", "Noche", "Día",
        "Boda", "Cóctel", "Casual", "Ejecutivo",
        
        # Características
        "Manga Larga", "Manga Corta", "Sin Mangas", "Cuello Alto",
        "Escote", "Estampado", "Liso", "Rayas", "Flores",
        "Lunares", "Oversize", "Ajustado", "Holgado",
        
        # Materiales/Texturas
        "Algodón", "Lino", "Seda", "Satín", "Lana",
        "Denim", "Cuero", "Encaje", "Tejido",
        
        # Colores principales
        "Negro", "Blanco", "Gris", "Azul", "Rojo",
        "Verde", "Amarillo", "Rosa", "Morado", "Beige",
        "Café", "Naranja", "Vinotinto",
        
        # Temporada
        "Primavera", "Verano", "Otoño", "Invierno",
        "Entretiempo"
    ]
    
    # Tags automáticos que siempre se incluyen
    auto_tags = [gender, product_type]
    
    print("\n" + "="*80)
    print(f"{Colors.BOLD}SELECCIÓN DE TAGS{Colors.RESET}")
    print("="*80)
    print(f"\n{Colors.GREEN}Tags automáticos:{Colors.RESET} {', '.join(auto_tags)}")
    print(f"\n{Colors.CYAN}Selecciona tags adicionales (puedes elegir varios){Colors.RESET}")
    print(f"{Colors.YELLOW}Ingresa los números separados por comas (ej: 1,5,12,23){Colors.RESET}")
    print(f"{Colors.YELLOW}o presiona Enter para continuar sin tags adicionales{Colors.RESET}\n")
    
    # Mostrar tags disponibles en columnas
    print(f"{Colors.BOLD}TAGS DISPONIBLES:{Colors.RESET}\n")
    
    # Organizar en 3 columnas
    cols = 3
    rows = (len(available_tags) + cols - 1) // cols
    
    for row in range(rows):
        line = ""
        for col in range(cols):
            idx = row + col * rows
            if idx < len(available_tags):
                num = idx + 1
                tag = available_tags[idx]
                # Resaltar si ya está en auto_tags
                if tag in auto_tags:
                    line += f"  {Colors.GREEN}{num:2d}. {tag:18s}{Colors.RESET}"
                else:
                    line += f"  {num:2d}. {tag:18s}"
        print(line)
    
    print()
    
    # Solicitar selección
    selection = get_input("Números de tags", allow_empty=True)
    
    selected_tags = auto_tags.copy()
    
    if selection:
        try:
            # Parsear números
            numbers = [int(n.strip()) for n in selection.split(',')]
            
            # Validar y agregar tags
            for num in numbers:
                if 1 <= num <= len(available_tags):
                    tag = available_tags[num - 1]
                    if tag not in selected_tags:
                        selected_tags.append(tag)
                else:
                    print_warning(f"Número {num} fuera de rango, ignorado")
        
        except ValueError:
            print_error("Formato inválido. Se usarán solo los tags automáticos")
    
    print_success(f"Tags seleccionados: {', '.join(selected_tags)}")
    
    return selected_tags

def collect_product_data(supabase: Client):
    """Recolecta datos del producto de forma interactiva"""
    
    print_header("NUEVO PRODUCTO")
    
    product = {}
    
    # 1. Nombre
    print_step("1. Nombre del producto")
    product['name'] = get_input("Nombre")
    print_success(f"Nombre: {product['name']}")
    
    # 2. Género
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 2. Género del producto")
    gender_options = ["Hombre", "Mujer", "Unisex"]
    product['gender'] = get_choice("Selecciona el género:", gender_options)
    print_success(f"Género: {product['gender']}")
    
    # 3. Tipo/Categoría
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 3. Tipo/Categoría del producto")
    type_options = [
        "Camisas", "Pantalones", "Chaquetas", "Vestidos", "Faldas",
        "Blusas", "Tops", "Suéteres", "Abrigos", "Shorts",
        "Zapatos", "Accesorios", "Bolsos", "Conjuntos", "Monos",
        "Bodys", "Básicos", "Otro"
    ]
    product['type'] = get_choice("Selecciona el tipo:", type_options)
    
    if product['type'] == "Otro":
        product['type'] = get_input("Especifica el tipo")
    
    print_success(f"Tipo: {product['type']}")
    
    # 4. Descripción
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 4. Descripción del producto")
    product['description'] = get_input("Descripción", allow_empty=True)
    if product['description']:
        print_success(f"Descripción: {product['description'][:50]}...")
    else:
        print_info("Sin descripción")
    
    # 5. Precio
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 5. Precio del producto")
    product['price'] = get_number("Precio (COP)", default=100000, min_value=0)
    print_success(f"Precio: ${product['price']:,.0f} COP")
    
    # 6. Stock
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 6. Stock disponible")
    product['stock'] = int(get_number("Cantidad en stock", default=10, min_value=0))
    print_success(f"Stock: {product['stock']} unidades")
    
    # 7. Imágenes
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 7. Imágenes del producto")
    product['images'] = select_product_images(supabase)
    
    # 8. Tags (selección múltiple por categorías)
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 8. Tags para búsqueda")
    product['tags'] = select_tags(product['gender'], product['type'])
    
    # 9. Visibilidad para invitados
    print(f"\n{Colors.BLUE}▶{Colors.RESET} 9. Visibilidad")
    product['is_visible_to_guest'] = confirm("¿Visible para usuarios invitados?")
    print_success(f"Visible para invitados: {'Sí' if product['is_visible_to_guest'] else 'No'}")
    
    return product

def select_product_images(supabase: Client):
    """Permite seleccionar las imágenes del producto"""
    
    while True:
        # Listar carpetas disponibles localmente
        print_info("Buscando carpetas de imágenes locales...")
        folders = list_available_product_folders_local()
        
        if not folders:
            print_error(f"No se encontraron carpetas de productos en {LOCAL_IMAGES_DIR}")
            if confirm("¿Deseas continuar sin imágenes?"):
                return []
            else:
                print_info(f"Por favor verifica que existan carpetas product_XX en {LOCAL_IMAGES_DIR}")
                sys.exit(0)
        
        print_success(f"Carpetas disponibles: {len(folders)}")
        print("\nCarpetas encontradas:")
        for i, folder in enumerate(folders, 1):
            print(f"  {i}. {folder}")
        
        # Solicitar carpeta
        folder_input = get_input(
            "\nIngresa el nombre de la carpeta (ej: product_01) o el número",
            allow_empty=False
        )
        
        # Permitir selección por número
        try:
            folder_num = int(folder_input)
            if 1 <= folder_num <= len(folders):
                selected_folder = folders[folder_num - 1]
            else:
                print_error(f"Número fuera de rango. Debe estar entre 1 y {len(folders)}")
                continue
        except ValueError:
            selected_folder = folder_input.strip()
        
        # Validar que la carpeta existe localmente
        if selected_folder not in folders:
            print_error(f"La carpeta '{selected_folder}' no existe localmente")
            print_warning("Carpetas válidas: " + ", ".join(folders))
            if not confirm("¿Deseas intentar de nuevo?"):
                return []
            continue
        
        # Mostrar imágenes locales disponibles
        local_folder_path = LOCAL_IMAGES_DIR / selected_folder
        local_images = list(local_folder_path.glob('*.avif')) + \
                      list(local_folder_path.glob('*.jpg')) + \
                      list(local_folder_path.glob('*.jpeg')) + \
                      list(local_folder_path.glob('*.png')) + \
                      list(local_folder_path.glob('*.webp'))
        
        if not local_images:
            print_error(f"No se encontraron imágenes en la carpeta local '{selected_folder}'")
            if not confirm("¿Deseas seleccionar otra carpeta?"):
                return []
            continue
        
        # Mostrar imágenes encontradas localmente
        print_success(f"Se encontraron {len(local_images)} imágenes localmente:")
        for i, img_path in enumerate(local_images, 1):
            print(f"  {i}. {img_path.name}")
        print()
        
        # Obtener URLs del bucket de Supabase
        print_info(f"Obteniendo URLs de Supabase para {selected_folder}...")
        images = get_images_from_folder(supabase, selected_folder)
        
        if not images:
            print_warning(f"No se encontraron imágenes en el bucket de Supabase para '{selected_folder}'")
            print_info("Las imágenes locales existen pero no están en el bucket")
            if confirm("¿Deseas continuar sin imágenes (deberás subirlas después)?"):
                return []
            if not confirm("¿Deseas seleccionar otra carpeta?"):
                return []
            continue
        
        # Mostrar imágenes encontradas
        print_success(f"Se encontraron {len(images)} imágenes:")
        for i, img_url in enumerate(images, 1):
            filename = img_url.split('/')[-1]
            print(f"  {i}. {filename}")
        
        if confirm("¿Usar estas imágenes?"):
            return images
        else:
            if not confirm("¿Deseas seleccionar otra carpeta?"):
                return []

def display_product_summary(product):
    """Muestra un resumen del producto antes de guardarlo"""
    print_header("RESUMEN DEL PRODUCTO")
    
    print(f"{Colors.BOLD}Nombre:{Colors.RESET}        {product['name']}")
    print(f"{Colors.BOLD}Género:{Colors.RESET}        {product['gender']}")
    print(f"{Colors.BOLD}Tipo:{Colors.RESET}          {product['type']}")
    print(f"{Colors.BOLD}Descripción:{Colors.RESET}   {product['description'] if product['description'] else '(sin descripción)'}")
    print(f"{Colors.BOLD}Precio:{Colors.RESET}        ${product['price']:,.0f} COP")
    print(f"{Colors.BOLD}Stock:{Colors.RESET}         {product['stock']} unidades")
    print(f"{Colors.BOLD}Imágenes:{Colors.RESET}      {len(product['images'])} imágenes")
    print(f"{Colors.BOLD}Tags:{Colors.RESET}          {', '.join(product['tags']) if product['tags'] else '(sin tags)'}")
    print(f"{Colors.BOLD}Invitados:{Colors.RESET}     {'Sí' if product['is_visible_to_guest'] else 'No'}")
    print()

def main():
    print_header("ELITE COUTURE - AÑADIR PRODUCTOS")
    print_info("Este script te permitirá añadir productos de forma interactiva")
    print_info("Presiona Ctrl+C en cualquier momento para cancelar")
    print()
    
    # Conectar a Supabase
    print_step("Conectando a Supabase...")
    try:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
        print_success("Conectado a Supabase")
    except Exception as e:
        print_error(f"Error conectando a Supabase: {str(e)}")
        sys.exit(1)
    
    products_added = 0
    
    # Loop principal - permite añadir múltiples productos
    while True:
        try:
            # Recolectar datos del producto
            product_data = collect_product_data(supabase)
            
            # Agregar campos adicionales
            product_data['uuid'] = str(uuid_lib.uuid4())
            product_data['created_at'] = int(datetime.now().timestamp() * 1000)
            
            # Mostrar resumen
            display_product_summary(product_data)
            
            # Confirmar guardado
            if not confirm("¿Guardar este producto en Supabase?"):
                print_warning("Producto descartado")
                if not confirm("¿Deseas crear otro producto?"):
                    break
                continue
            
            # Guardar en Supabase
            print_step("Guardando producto en Supabase...")
            success, result = create_product(supabase, product_data)
            
            if success:
                products_added += 1
                print_success(f"Producto guardado exitosamente (UUID: {product_data['uuid'][:8]}...)")
                print()
            else:
                print_error(f"Error al guardar: {result}")
                print()
            
            # Preguntar si desea añadir otro producto
            if not confirm("¿Deseas añadir otro producto?"):
                break
        
        except KeyboardInterrupt:
            print("\n")
            print_warning("Operación cancelada por el usuario")
            break
        except Exception as e:
            print_error(f"Error inesperado: {str(e)}")
            if not confirm("¿Deseas continuar?"):
                break
    
    # Resumen final
    print_header("FINALIZADO")
    print_success(f"Productos añadidos en esta sesión: {products_added}")
    print()
    print_info("¡Gracias por usar el sistema de gestión de productos!")
    print()

if __name__ == "__main__":
    main()
