"""
Script de Migración de Productos a Supabase
Elite Couture - Carga masiva de productos con sus imágenes

Este script:
1. Lee el archivo con las URLs de las imágenes subidas
2. Crea productos realistas de moda femenina
3. Inserta todos los productos en la tabla 'products' de Supabase
4. Asigna las URLs de imágenes correctas a cada producto

Requisitos:
    pip install supabase python-dotenv

Uso:
    python migrate_products_to_supabase.py
"""

import os
import sys
import json
from datetime import datetime
from pathlib import Path
import uuid

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
    print(f"{Colors.GREEN}✓{Colors.RESET} {text}")

def print_error(text):
    print(f"{Colors.RED}✗{Colors.RESET} {text}")

def print_warning(text):
    print(f"{Colors.YELLOW}⚠{Colors.RESET} {text}")

def print_info(text):
    print(f"{Colors.CYAN}ℹ{Colors.RESET} {text}")

def print_step(text):
    print(f"{Colors.BLUE}▶{Colors.RESET} {text}")

# Credenciales de Supabase
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print_error("Faltan credenciales de Supabase en .env")
    print_info("Asegúrate de tener SUPABASE_URL y SUPABASE_ANON_KEY")
    sys.exit(1)

# Archivo con las URLs de las imágenes
SCRIPT_DIR = Path(__file__).parent
URLS_FILE = SCRIPT_DIR / "uploaded_images_urls.json"

# =============================================================================
# DATOS DE PRODUCTOS
# =============================================================================

# Productos de moda femenina realistas
PRODUCTS_DATA = [
    {
        "name": "Vestido Midi Floral Primavera",
        "description": "Vestido midi con estampado floral, perfecto para ocasiones especiales. Corte favorecedor con cinturón incluido.",
        "price": 189000,
        "stock": 15,
        "category": "Vestidos",
        "tags": ["vestido", "floral", "midi", "primavera"],
        "visible_to_guests": True
    },
    {
        "name": "Blusa Elegante Manga Larga",
        "description": "Blusa de corte elegante con manga larga y cuello en V. Ideal para looks de oficina o casuales sofisticados.",
        "price": 95000,
        "stock": 20,
        "category": "Blusas",
        "tags": ["blusa", "elegante", "manga larga", "oficina"],
        "visible_to_guests": True
    },
    {
        "name": "Top Crop Moderno",
        "description": "Top crop de diseño moderno y juvenil. Combina perfecto con jeans de tiro alto o faldas midi.",
        "price": 65000,
        "stock": 25,
        "category": "Tops",
        "tags": ["top", "crop", "moderno", "casual"],
        "visible_to_guests": True
    },
    {
        "name": "Pantalón Palazzo Negro",
        "description": "Pantalón palazzo de corte amplio y elegante. Material fluido, ideal para eventos formales y salidas nocturnas.",
        "price": 135000,
        "stock": 12,
        "category": "Pantalones",
        "tags": ["pantalón", "palazzo", "negro", "elegante"],
        "visible_to_guests": True
    },
    {
        "name": "Camiseta Básica Premium",
        "description": "Camiseta básica de algodón premium. Corte perfecto y gran variedad de colores. Esencial en todo guardarropa.",
        "price": 45000,
        "stock": 30,
        "category": "Básicos",
        "tags": ["camiseta", "básica", "algodón", "esencial"],
        "visible_to_guests": True
    },
    {
        "name": "Falda Plisada Romántica",
        "description": "Falda plisada de longitud midi con caída romántica. Perfect para looks femeninos y sofisticados.",
        "price": 110000,
        "stock": 18,
        "category": "Faldas",
        "tags": ["falda", "plisada", "midi", "romántica"],
        "visible_to_guests": True
    },
    {
        "name": "Blazer Estructurado Beige",
        "description": "Blazer de corte estructurado en tono beige neutro. Prenda versátil para looks formales y casual chic.",
        "price": 225000,
        "stock": 10,
        "category": "Chaquetas",
        "tags": ["blazer", "beige", "formal", "estructurado"],
        "visible_to_guests": True
    },
    {
        "name": "Conjunto Deportivo Chic",
        "description": "Conjunto deportivo de dos piezas con diseño moderno. Cómodo y elegante para actividades casuales.",
        "price": 155000,
        "stock": 14,
        "category": "Conjuntos",
        "tags": ["conjunto", "deportivo", "cómodo", "moderno"],
        "visible_to_guests": True
    },
    {
        "name": "Vestido Cóctel Negro",
        "description": "Vestido corto tipo cóctel en negro clásico. Diseño atemporal perfecto para eventos formales.",
        "price": 245000,
        "stock": 8,
        "category": "Vestidos",
        "tags": ["vestido", "cóctel", "negro", "formal"],
        "visible_to_guests": True
    },
    {
        "name": "Kimono Estampado Boho",
        "description": "Kimono ligero con estampados bohemios. Ideal para looks veraniegos y playeros.",
        "price": 85000,
        "stock": 22,
        "category": "Chaquetas",
        "tags": ["kimono", "boho", "verano", "playero"],
        "visible_to_guests": True
    },
    {
        "name": "Jumpsuit Elegante Verde",
        "description": "Jumpsuit de pierna ancha en tono verde esmeralda. Statement piece para ocasiones especiales.",
        "price": 198000,
        "stock": 9,
        "category": "Monos",
        "tags": ["jumpsuit", "verde", "elegante", "pierna ancha"],
        "visible_to_guests": True
    },
    {
        "name": "Cardigan Largo Tejido",
        "description": "Cardigan largo de punto con textura suave. Perfecto para entretiempo y looks cozy.",
        "price": 125000,
        "stock": 16,
        "category": "Chaquetas",
        "tags": ["cardigan", "tejido", "largo", "cozy"],
        "visible_to_guests": True
    },
    {
        "name": "Falda Mini Denim",
        "description": "Falda mini en denim clásico con botones frontales. Estilo casual juvenil y versátil.",
        "price": 75000,
        "stock": 28,
        "category": "Faldas",
        "tags": ["falda", "mini", "denim", "casual"],
        "visible_to_guests": True
    },
    {
        "name": "Blusa Satinada Rosada",
        "description": "Blusa en satín con acabado brillante en tono rosa suave. Sofisticación para looks de noche.",
        "price": 115000,
        "stock": 13,
        "category": "Blusas",
        "tags": ["blusa", "satín", "rosa", "brillante"],
        "visible_to_guests": True
    },
    {
        "name": "Pantalón Cargo Tendencia",
        "description": "Pantalón cargo con múltiples bolsillos y corte moderno. Combina comodidad y estilo urbano.",
        "price": 145000,
        "stock": 19,
        "category": "Pantalones",
        "tags": ["pantalón", "cargo", "urbano", "tendencia"],
        "visible_to_guests": True
    },
    {
        "name": "Top Halter Verano",
        "description": "Top tipo halter con escote favorecedor. Ideal para looks veraniegos y eventos al aire libre.",
        "price": 68000,
        "stock": 24,
        "category": "Tops",
        "tags": ["top", "halter", "verano", "escote"],
        "visible_to_guests": True
    },
    {
        "name": "Vestido Maxi Bohemio",
        "description": "Vestido largo estilo bohemio con estampados étnicos. Comodidad y estilo para el día a día.",
        "price": 175000,
        "stock": 11,
        "category": "Vestidos",
        "tags": ["vestido", "maxi", "bohemio", "étnico"],
        "visible_to_guests": True
    },
    {
        "name": "Camisa Oversized Blanca",
        "description": "Camisa blanca de corte oversized en algodón premium. Básico reinventado para looks modernos.",
        "price": 98000,
        "stock": 21,
        "category": "Camisas",
        "tags": ["camisa", "oversized", "blanca", "básica"],
        "visible_to_guests": True
    },
    {
        "name": "Short Tiro Alto Lino",
        "description": "Short de tiro alto en lino natural. Fresco y cómodo para temporadas cálidas.",
        "price": 72000,
        "stock": 26,
        "category": "Shorts",
        "tags": ["short", "lino", "tiro alto", "verano"],
        "visible_to_guests": True
    },
    {
        "name": "Suéter Cuello Tortuga",
        "description": "Suéter de cuello alto en lana suave. Clásico invernal para looks sofisticados.",
        "price": 135000,
        "stock": 14,
        "category": "Suéteres",
        "tags": ["suéter", "cuello alto", "lana", "invierno"],
        "visible_to_guests": True
    },
    {
        "name": "Body Encaje Negro",
        "description": "Body de encaje con detalles delicados. Pieza versátil para looks de día o noche.",
        "price": 88000,
        "stock": 17,
        "category": "Bodys",
        "tags": ["body", "encaje", "negro", "delicado"],
        "visible_to_guests": True
    },
    {
        "name": "Falda Lápiz Ejecutiva",
        "description": "Falda lápiz de corte ejecutivo. Elegancia y profesionalismo para el entorno laboral.",
        "price": 105000,
        "stock": 15,
        "category": "Faldas",
        "tags": ["falda", "lápiz", "ejecutiva", "formal"],
        "visible_to_guests": True
    },
    {
        "name": "Top Bustier Fiesta",
        "description": "Top bustier estructurado para ocasiones especiales. Diseño sofisticado con aros interiores.",
        "price": 125000,
        "stock": 10,
        "category": "Tops",
        "tags": ["top", "bustier", "fiesta", "estructurado"],
        "visible_to_guests": True
    },
    {
        "name": "Pantalón Wide Leg Café",
        "description": "Pantalón de pierna ancha en tono café tierra. Tendencia actual con máxima comodidad.",
        "price": 158000,
        "stock": 13,
        "category": "Pantalones",
        "tags": ["pantalón", "wide leg", "café", "tendencia"],
        "visible_to_guests": True
    },
    {
        "name": "Vestido Camisero Rayas",
        "description": "Vestido tipo camisero con estampado de rayas. Clásico atemporal con cinturón ajustable.",
        "price": 142000,
        "stock": 16,
        "category": "Vestidos",
        "tags": ["vestido", "camisero", "rayas", "clásico"],
        "visible_to_guests": True
    }
]

# =============================================================================
# FUNCIONES
# =============================================================================

def load_image_urls() -> dict:
    """Carga las URLs de las imágenes desde el archivo JSON"""
    if not URLS_FILE.exists():
        print_error(f"No se encontró el archivo de URLs: {URLS_FILE}")
        print_info("Ejecuta primero: python upload_all_product_images.py")
        sys.exit(1)
    
    with open(URLS_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    return data['products']

def create_product_record(product_data: dict, image_urls: list, product_num: int) -> dict:
    """Crea un registro de producto completo para Supabase"""
    return {
        'uuid': str(uuid.uuid4()),
        'name': product_data['name'],
        'description': product_data['description'],
        'price': product_data['price'],
        'stock': product_data['stock'],
        'type': product_data['category'],  # 'category' se mapea a 'type' en Supabase
        'gender': 'Mujer',  # Todos los productos son de moda femenina
        'images': image_urls,  # Array de URLs (usando 'images' no 'image_urls')
        'tags': product_data['tags'],
        'is_visible_to_guest': product_data['visible_to_guests'],
        'created_at': int(datetime.now().timestamp() * 1000)  # Timestamp en milisegundos
    }

# =============================================================================
# SCRIPT PRINCIPAL
# =============================================================================

def main():
    print("=" * 80)
    print("  MIGRACIÓN DE PRODUCTOS A SUPABASE")
    print("  Elite Couture - Carga masiva de catálogo")
    print("=" * 80)
    print()
    
    # Cargar URLs de imágenes
    print_step("Cargando URLs de imágenes subidas...")
    try:
        image_urls_data = load_image_urls()
        print_success(f"Cargadas URLs de {len(image_urls_data)} productos")
    except Exception as e:
        print_error(f"Error cargando URLs: {str(e)}")
        sys.exit(1)
    
    print()
    
    # Verificar que tenemos datos para todos los productos
    if len(PRODUCTS_DATA) > len(image_urls_data):
        print_error(f"Faltan imágenes: {len(PRODUCTS_DATA)} productos pero solo {len(image_urls_data)} carpetas con imágenes")
        sys.exit(1)
    
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
    print(f"{Colors.YELLOW}{Colors.BOLD}¿Deseas insertar {len(PRODUCTS_DATA)} productos en la base de datos?{Colors.RESET}")
    print_warning("Esto agregará productos a la tabla 'products'")
    response = input("Escribe 'SI' para continuar: ").strip().upper()
    
    if response != 'SI':
        print_warning("Operación cancelada por el usuario")
        sys.exit(0)
    
    print()
    print("=" * 80)
    print("  INSERTANDO PRODUCTOS")
    print("=" * 80)
    print()
    
    # Estadísticas
    stats = {
        'success': 0,
        'failed': 0,
        'total_price': 0
    }
    
    inserted_products = []
    
    # Insertar cada producto
    for idx, product_data in enumerate(PRODUCTS_DATA, 1):
        product_key = f"product_{idx:02d}"
        
        print(f"{Colors.CYAN}[{idx}/{len(PRODUCTS_DATA)}]{Colors.RESET} {product_data['name']}")
        
        try:
            # Obtener URLs de imágenes para este producto
            if product_key in image_urls_data:
                image_urls = [img['url'] for img in image_urls_data[product_key]]
            else:
                print_warning(f"  No se encontraron imágenes para {product_key}, usando lista vacía")
                image_urls = []
            
            # Crear registro
            product_record = create_product_record(product_data, image_urls, idx)
            
            # Insertar en Supabase
            result = supabase.table('products').insert(product_record).execute()
            
            print_success(f"  Insertado con UUID: {product_record['uuid']}")
            print_info(f"  Precio: ${product_record['price']:,} COP | Stock: {product_record['stock']} | Imágenes: {len(image_urls)}")
            
            stats['success'] += 1
            stats['total_price'] += product_record['price']
            inserted_products.append(product_record)
            
        except Exception as e:
            print_error(f"  Error: {str(e)}")
            stats['failed'] += 1
        
        print()
    
    # Resumen final
    print("=" * 80)
    print("  RESUMEN DE MIGRACIÓN")
    print("=" * 80)
    print()
    print(f"  Productos insertados:    {Colors.GREEN}{stats['success']}{Colors.RESET}")
    print(f"  Productos fallidos:      {Colors.RED if stats['failed'] > 0 else Colors.GREEN}{stats['failed']}{Colors.RESET}")
    print(f"  Valor total inventario:  ${stats['total_price']:,} COP")
    print(f"  Precio promedio:         ${stats['total_price'] // max(stats['success'], 1):,} COP")
    print()
    
    if stats['failed'] == 0:
        print(f"{Colors.GREEN}{Colors.BOLD}TODOS LOS PRODUCTOS SE INSERTARON EXITOSAMENTE{Colors.RESET}")
        print()
        print_info("Puedes verificar los productos en tu dashboard de Supabase:")
        print(f"   {SUPABASE_URL.replace('https://', 'https://app.')}/project/_/editor/")
    else:
        print(f"{Colors.YELLOW}Se completó con {stats['failed']} errores{Colors.RESET}")
    
    print()
    
    # Guardar resumen
    summary_file = SCRIPT_DIR / "migration_summary.json"
    summary_data = {
        'timestamp': datetime.now().isoformat(),
        'stats': stats,
        'products': inserted_products
    }
    
    with open(summary_file, 'w', encoding='utf-8') as f:
        json.dump(summary_data, f, indent=2, ensure_ascii=False)
    
    print_success(f"Resumen guardado en: {summary_file}")
    print()
    print("=" * 80)

if __name__ == "__main__":
    main()
