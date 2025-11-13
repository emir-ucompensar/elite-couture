"""
Script para ELIMINAR todos los productos de Supabase
Elite Couture - Limpieza completa de la tabla 'products'

ADVERTENCIA: Este script eliminará TODOS los productos de la base de datos

Requisitos:
    pip install supabase python-dotenv

Uso:
    python delete_all_products.py
"""

import os
import sys
from pathlib import Path

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
    print(f"{Colors.GREEN}{Colors.RESET} {text}")

def print_error(text):
    print(f"{Colors.RED}{Colors.RESET} {text}")

def print_warning(text):
    print(f"{Colors.YELLOW}{Colors.RESET} {text}")

def print_info(text):
    print(f"{Colors.CYAN}{Colors.RESET} {text}")

# Credenciales de Supabase
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print_error("Faltan credenciales de Supabase en .env")
    print_info("Asegúrate de tener SUPABASE_URL y SUPABASE_ANON_KEY")
    sys.exit(1)

# =============================================================================
# SCRIPT PRINCIPAL
# =============================================================================

def main():
    print("=" * 80)
    print(f"  {Colors.RED}{Colors.BOLD}ELIMINACIÓN TOTAL DE PRODUCTOS{Colors.RESET}")
    print("  Elite Couture - Limpieza de base de datos")
    print("=" * 80)
    print()
    
    # Conectar a Supabase
    print("Conectando a Supabase...")
    try:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
        print_success("Conectado a Supabase")
    except Exception as e:
        print_error(f"Error conectando a Supabase: {str(e)}")
        sys.exit(1)
    
    print()
    
    # Obtener conteo actual de productos
    print("Obteniendo información actual...")
    try:
        response = supabase.table('products').select('*', count='exact').execute()
        total_products = len(response.data)
        
        if total_products == 0:
            print_info("No hay productos en la base de datos")
            print()
            return
        
        print_warning(f"Productos encontrados: {total_products}")
        print()
        
        # Mostrar algunos productos
        print("Primeros 5 productos que serán eliminados:")
        for i, product in enumerate(response.data[:5], 1):
            print(f"   {i}. {product['name']} (UUID: {product['uuid'][:8]}...)")
        
        if total_products > 5:
            print(f"   ... y {total_products - 5} productos más")
        
        print()
        
    except Exception as e:
        print_error(f"Error obteniendo productos: {str(e)}")
        sys.exit(1)
    
    # CONFIRMACIÓN CRÍTICA
    print("=" * 80)
    print(f"{Colors.RED}{Colors.BOLD}ADVERTENCIA CRÍTICA{Colors.RESET}")
    print(f"{Colors.YELLOW}Esta acción eliminará TODOS los {total_products} productos de la base de datos.{Colors.RESET}")
    print(f"{Colors.YELLOW}Esta acción NO SE PUEDE DESHACER.{Colors.RESET}")
    print("=" * 80)
    print()
    
    # Primera confirmación
    response1 = input(f"{Colors.YELLOW}¿Estás SEGURO de que quieres eliminar TODOS los productos? (escribe 'SI'): {Colors.RESET}").strip().upper()
    
    if response1 != 'SI':
        print_warning("Operación cancelada por el usuario")
        sys.exit(0)
    
    # Segunda confirmación
    print()
    response2 = input(f"{Colors.RED}Segunda confirmación - Escribe 'ELIMINAR TODO' para proceder: {Colors.RESET}").strip().upper()
    
    if response2 != 'ELIMINAR TODO':
        print_warning("Operación cancelada por el usuario")
        sys.exit(0)
    
    print()
    print("=" * 80)
    print(f"  {Colors.RED}ELIMINANDO PRODUCTOS...{Colors.RESET}")
    print("=" * 80)
    print()
    
    # Eliminar todos los productos
    try:
        # Supabase: delete sin filtros elimina todos los registros
        result = supabase.table('products').delete().neq('uuid', '00000000-0000-0000-0000-000000000000').execute()
        
        print_success(f"{Colors.GREEN}{Colors.BOLD}TODOS LOS PRODUCTOS HAN SIDO ELIMINADOS{Colors.RESET}")
        print()
        
        # Verificar que la tabla esté vacía
        verify = supabase.table('products').select('*', count='exact').execute()
        remaining = len(verify.data)
        
        if remaining == 0:
            print_success(f"Verificado: La tabla 'products' está ahora vacía (0 productos)")
        else:
            print_warning(f"Atención: Quedan {remaining} productos en la tabla")
        
        print()
        
    except Exception as e:
        print_error(f"Error al eliminar productos: {str(e)}")
        print_info("Puede que necesites permisos adicionales o que haya restricciones de foreign keys")
        sys.exit(1)
    
    print("=" * 80)
    print(f"  {Colors.GREEN}LIMPIEZA COMPLETADA{Colors.RESET}")
    print("=" * 80)
    print()
    print_info("La base de datos está lista para recibir nuevos productos")
    print()

if __name__ == "__main__":
    main()
