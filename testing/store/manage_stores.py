
import json
import os
import sys
from pathlib import Path
from dotenv import load_dotenv

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: Librería 'supabase' no instalada")
    print("   Instala con: pip install supabase")
    sys.exit(1)

# Colores y helpers estilo add_product_interactive.py
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

def print_step(text):
    print(f"{Colors.BLUE}▶{Colors.RESET} {text}")

def print_header(text):
    print(f"\n{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{text.center(80)}{Colors.RESET}")
    print(f"{Colors.MAGENTA}{Colors.BOLD}{'='*80}{Colors.RESET}\n")

# Cargar .env
env_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(env_path)

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    print_error("Faltan credenciales de Supabase en .env")
    print_info("Asegúrate de tener SUPABASE_URL y SUPABASE_ANON_KEY")
    sys.exit(1)

SCRIPT_DIR = Path(__file__).parent
STORES_JSON_PATH = SCRIPT_DIR / "stores.json"

def load_stores_json():
    if not STORES_JSON_PATH.exists():
        print_error("El archivo stores.json no existe.")
        return []
    with open(STORES_JSON_PATH, "r", encoding="utf-8") as file:
        try:
            return json.load(file)
        except json.JSONDecodeError:
            print_error("Error al leer el archivo stores.json. Asegúrate de que el formato sea válido.")
            return []

def get_supabase_client():
    return create_client(SUPABASE_URL, SUPABASE_ANON_KEY)

def list_stores_supabase(supabase):
    print_step("Consultando tiendas en Supabase...")
    try:
        response = supabase.table('stores').select('*').execute()
        stores = response.data
        if not stores:
            print_info("No hay tiendas registradas en Supabase.")
            return []
        print_header("TIENDAS EN SUPABASE")
        for store in stores:
            print(f"{Colors.BOLD}ID:{Colors.RESET} {store['id']}")
            print(f"{Colors.BOLD}Nombre:{Colors.RESET} {store['name']}")
            print(f"{Colors.BOLD}Dirección:{Colors.RESET} {store['address']}")
            print(f"{Colors.BOLD}Teléfono:{Colors.RESET} {store['phone']}")
            print(f"{Colors.BOLD}Horario:{Colors.RESET} {store['hours']}")
            print(f"{Colors.BOLD}Latitud:{Colors.RESET} {store['latitude']}")
            print(f"{Colors.BOLD}Longitud:{Colors.RESET} {store['longitude']}\n")
        return stores
    except Exception as e:
        print_error(f"Error consultando tiendas: {str(e)}")
        return []

def compare_and_prompt_load_new_stores(supabase):
    json_stores = load_stores_json()
    try:
        response = supabase.table('stores').select('id').execute()
        supabase_ids = {s['id'] for s in response.data}
    except Exception as e:
        print_error(f"Error consultando tiendas: {str(e)}")
        return
    new_stores = [store for store in json_stores if store['id'] not in supabase_ids]
    if not new_stores:
        print_info("No hay tiendas nuevas para cargar.")
        return
    print_header("TIENDAS NUEVAS EN JSON (NO EN SUPABASE)")
    for store in new_stores:
        print(f"{Colors.BOLD}ID:{Colors.RESET} {store['id']} | {store['name']}")
    resp = input("¿Deseas cargar estas tiendas a Supabase? (s/n): ").strip().lower()
    if resp in ['s', 'si', 'sí', 'y', 'yes']:
        try:
            for store in new_stores:
                supabase.table('stores').insert(store).execute()
            print_success(f"Se añadieron {len(new_stores)} tiendas nuevas a Supabase.")
        except Exception as e:
            print_error(f"Error insertando tiendas: {str(e)}")
    else:
        print_info("No se cargaron nuevas tiendas.")

def delete_store_supabase(supabase):
    print_step("Eliminar tienda de Supabase")
    try:
        response = supabase.table('stores').select('id, name').execute()
        stores = response.data
        if not stores:
            print_info("No hay tiendas para borrar.")
            return
        print_header("TIENDAS DISPONIBLES PARA BORRAR")
        for store in stores:
            print(f"{Colors.BOLD}ID:{Colors.RESET} {store['id']} | {store['name']}")
        store_id = input("Ingresa el ID de la tienda a borrar: ").strip()
        if not store_id.isdigit():
            print_error("ID inválido.")
            return
        store_id = int(store_id)
        confirm = input(f"¿Seguro que deseas borrar la tienda ID {store_id}? (s/n): ").strip().lower()
        if confirm not in ['s', 'si', 'sí', 'y', 'yes']:
            print_info("Operación cancelada.")
            return
        result = supabase.table('stores').delete().eq('id', store_id).execute()
        if result.data:
            print_success(f"Tienda ID {store_id} eliminada de Supabase.")
        else:
            print_error("No se encontró la tienda o no se pudo borrar.")
    except Exception as e:
        print_error(f"Error eliminando tienda: {str(e)}")

def main():
    supabase = get_supabase_client()
    while True:
        print_header("GESTIÓN DE TIENDAS (SUPABASE)")
        print("1. Listar tiendas (Supabase)")
        print("2. Cargar nuevas tiendas desde JSON")
        print("3. Borrar tienda de Supabase")
        print("4. Salir")

        choice = input("Selecciona una opción: ").strip()

        if choice == "1":
            list_stores_supabase(supabase)
        elif choice == "2":
            compare_and_prompt_load_new_stores(supabase)
        elif choice == "3":
            delete_store_supabase(supabase)
        elif choice == "4":
            print_info("Saliendo del programa.")
            break
        else:
            print_error("Opción no válida. Intenta de nuevo.")

if __name__ == "__main__":
    main()