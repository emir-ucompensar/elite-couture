"""
Script para verificar los géneros y categorías de productos en Supabase
"""
import os
import sys
from pathlib import Path

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: pip install supabase")
    sys.exit(1)

try:
    from dotenv import load_dotenv
except ImportError:
    print("Error: pip install python-dotenv")
    sys.exit(1)

# Cargar variables de entorno
env_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(env_path)

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print("Error: Faltan credenciales de Supabase en .env")
    sys.exit(1)

# Conectar a Supabase
supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)

# Obtener todos los productos
response = supabase.table('products').select('*').execute()
products = response.data

print(f"\n{'='*80}")
print(f"TOTAL PRODUCTOS: {len(products)}")
print(f"{'='*80}\n")

# Agrupar por género
by_gender = {}
for product in products:
    gender = product.get('gender', 'Sin género')
    if gender not in by_gender:
        by_gender[gender] = []
    by_gender[gender].append(product)

print("PRODUCTOS POR GÉNERO:")
for gender, prods in by_gender.items():
    print(f"\n{gender}: {len(prods)} productos")
    print("-" * 40)
    for p in prods[:5]:  # Mostrar solo los primeros 5
        print(f"  - {p['name']} (Tipo: {p.get('type', 'N/A')})")
    if len(prods) > 5:
        print(f"  ... y {len(prods) - 5} más")

# Agrupar por tipo
print(f"\n{'='*80}")
print("PRODUCTOS POR TIPO:")
print(f"{'='*80}\n")

by_type = {}
for product in products:
    ptype = product.get('type', 'Sin tipo')
    if ptype not in by_type:
        by_type[ptype] = []
    by_type[ptype].append(product)

for ptype, prods in sorted(by_type.items()):
    print(f"\n{ptype}: {len(prods)} productos")
    for p in prods[:3]:
        print(f"  - {p['name']} (Género: {p.get('gender', 'N/A')})")
    if len(prods) > 3:
        print(f"  ... y {len(prods) - 3} más")

# Buscar productos que podrían ser de hombre por nombre
print(f"\n{'='*80}")
print("ANÁLISIS: Productos que podrían ser de HOMBRE:")
print(f"{'='*80}\n")

keywords_hombre = ['hombre', 'masculino', 'caballero', 'men', 'male']
posibles_hombre = []

for product in products:
    name_lower = product['name'].lower()
    desc_lower = (product.get('description') or '').lower()
    tags_lower = ' '.join(product.get('tags', [])).lower()
    
    if any(kw in name_lower or kw in desc_lower or kw in tags_lower for kw in keywords_hombre):
        posibles_hombre.append(product)

if posibles_hombre:
    print(f"Encontrados {len(posibles_hombre)} productos que mencionan 'hombre':")
    for p in posibles_hombre:
        print(f"\n  UUID: {p['uuid']}")
        print(f"  Nombre: {p['name']}")
        print(f"  Género actual: {p.get('gender', 'N/A')}")
        print(f"  Tipo: {p.get('type', 'N/A')}")
        print(f"  Tags: {p.get('tags', [])}")
else:
    print("No se encontraron productos que mencionen 'hombre' en nombre/descripción/tags")

print(f"\n{'='*80}\n")
