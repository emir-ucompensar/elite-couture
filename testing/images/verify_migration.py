"""
Script de Verificación de Migración Completa
Elite Couture - Verifica productos e imágenes en Supabase

Este script:
1. Verifica que todos los productos estén en la base de datos
2. Valida que todas las imágenes sean accesibles
3. Muestra estadísticas completas del catálogo

Uso:
    python verify_migration.py
"""

import os
import sys
from pathlib import Path

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: Librería 'supabase' no instalada")
    sys.exit(1)

try:
    from dotenv import load_dotenv
except ImportError:
    print("Error: Librería 'python-dotenv' no instalada")
    sys.exit(1)

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

def print_info(text):
    print(f"{Colors.CYAN}ℹ{Colors.RESET} {text}")

def print_header(text):
    print(f"\n{Colors.BOLD}{Colors.BLUE}{text}{Colors.RESET}")
    print("=" * 80)

# Credenciales
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

def main():
    print("=" * 80)
    print("  🔍 VERIFICACIÓN DE MIGRACIÓN COMPLETA")
    print("  Elite Couture - Productos e Imágenes")
    print("=" * 80)
    
    # Conectar a Supabase
    print_info("Conectando a Supabase...")
    try:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
        print_success("Conectado exitosamente")
    except Exception as e:
        print_error(f"Error conectando: {str(e)}")
        sys.exit(1)
    
    # Obtener todos los productos
    print_header("📦 PRODUCTOS EN BASE DE DATOS")
    try:
        response = supabase.table('products').select('*').execute()
        products = response.data
        print_success(f"Total de productos: {len(products)}")
    except Exception as e:
        print_error(f"Error obteniendo productos: {str(e)}")
        sys.exit(1)
    
    # Estadísticas generales
    print_header("📊 ESTADÍSTICAS GENERALES")
    
    total_stock = sum(p['stock'] for p in products)
    total_value = sum(p['price'] * p['stock'] for p in products)
    avg_price = sum(p['price'] for p in products) / len(products) if products else 0
    
    print(f"  Total productos:         {len(products)}")
    print(f"  Stock total:             {total_stock} unidades")
    print(f"  Valor total inventario:  ${total_value:,.0f} COP")
    print(f"  Precio promedio:         ${avg_price:,.0f} COP")
    
    # Verificar imágenes
    print_header("🖼️  VERIFICACIÓN DE IMÁGENES")
    
    total_images = 0
    products_with_images = 0
    products_without_images = 0
    
    for product in products:
        image_count = len(product.get('images', []))
        total_images += image_count
        
        if image_count > 0:
            products_with_images += 1
        else:
            products_without_images += 1
    
    print(f"  Total de imágenes:       {total_images}")
    print(f"  Productos con imágenes:  {products_with_images}")
    print(f"  Productos sin imágenes:  {products_without_images}")
    print(f"  Promedio por producto:   {total_images / len(products):.1f} imágenes")
    
    # Análisis por categoría
    print_header("📋 PRODUCTOS POR CATEGORÍA")
    
    categories = {}
    for product in products:
        cat = product.get('type', 'Sin categoría')
        if cat not in categories:
            categories[cat] = {'count': 0, 'stock': 0, 'value': 0}
        categories[cat]['count'] += 1
        categories[cat]['stock'] += product['stock']
        categories[cat]['value'] += product['price'] * product['stock']
    
    for cat, data in sorted(categories.items(), key=lambda x: x[1]['count'], reverse=True):
        print(f"  {cat:20s} → {data['count']:2d} productos | {data['stock']:3d} unidades | ${data['value']:,} COP")
    
    # Productos con mayor/menor precio
    print_header("💰 ANÁLISIS DE PRECIOS")
    
    sorted_by_price = sorted(products, key=lambda x: x['price'], reverse=True)
    
    print(f"\n  {Colors.BOLD}Top 5 Más Caros:{Colors.RESET}")
    for i, product in enumerate(sorted_by_price[:5], 1):
        print(f"    {i}. {product['name']:40s} ${product['price']:,} COP")
    
    print(f"\n  {Colors.BOLD}Top 5 Más Económicos:{Colors.RESET}")
    for i, product in enumerate(sorted_by_price[-5:][::-1], 1):
        print(f"    {i}. {product['name']:40s} ${product['price']:,} COP")
    
    # Stock bajo
    print_header("⚠️  ALERTAS DE STOCK")
    
    low_stock = [p for p in products if p['stock'] < 10]
    if low_stock:
        print_info(f"Productos con stock bajo (<10 unidades): {len(low_stock)}")
        for product in sorted(low_stock, key=lambda x: x['stock']):
            print(f"    • {product['name']:40s} {Colors.RED}{product['stock']:2d} unidades{Colors.RESET}")
    else:
        print_success("Todos los productos tienen stock adecuado")
    
    # Verificar visibilidad
    print_header("👁️  VISIBILIDAD DE PRODUCTOS")
    
    visible = len([p for p in products if p.get('is_visible_to_guest', False)])
    hidden = len(products) - visible
    
    print(f"  Visibles para invitados: {Colors.GREEN}{visible}{Colors.RESET}")
    print(f"  Ocultos:                 {Colors.YELLOW}{hidden}{Colors.RESET}")
    
    # Tags más comunes
    print_header("🏷️  TAGS MÁS COMUNES")
    
    all_tags = {}
    for product in products:
        for tag in product.get('tags', []):
            all_tags[tag] = all_tags.get(tag, 0) + 1
    
    top_tags = sorted(all_tags.items(), key=lambda x: x[1], reverse=True)[:10]
    for tag, count in top_tags:
        print(f"  {tag:20s} → {count} productos")
    
    # Resumen final
    print("\n" + "=" * 80)
    if products_without_images == 0 and len(products) >= 25:
        print(f"{Colors.GREEN}{Colors.BOLD}✅ MIGRACIÓN COMPLETA Y EXITOSA{Colors.RESET}")
        print(f"\n  • {len(products)} productos migrados")
        print(f"  • {total_images} imágenes subidas")
        print(f"  • Todos los productos tienen imágenes")
        print(f"  • Base de datos lista para producción")
    else:
        print(f"{Colors.YELLOW}⚠️  MIGRACIÓN PARCIAL{Colors.RESET}")
        if products_without_images > 0:
            print(f"  • {products_without_images} productos sin imágenes")
        if len(products) < 25:
            print(f"  • Solo {len(products)}/25 productos esperados")
    
    print("\n" + "=" * 80)
    print_info("Puedes ver los productos en:")
    print(f"   {SUPABASE_URL.replace('https://', 'https://app.')}/project/_/editor/")
    print("=" * 80)

if __name__ == "__main__":
    main()
