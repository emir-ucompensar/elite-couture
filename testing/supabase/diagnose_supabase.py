"""
Script de diagnóstico para Supabase Storage
Verifica configuración y permisos del bucket
"""

from supabase import create_client, Client
import json

# Colores ANSI para Windows/PowerShell
class Colors:
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_success(text):
    print(f"{Colors.GREEN}[OK]{Colors.RESET} {text}")

def print_error(text):
    print(f"{Colors.RED}[ERROR]{Colors.RESET} {text}")

def print_warning(text):
    print(f"{Colors.YELLOW}[WARN]{Colors.RESET} {text}")

def print_info(text):
    print(f"{Colors.CYAN}[INFO]{Colors.RESET} {text}")

# Credenciales
SUPABASE_URL = "https://tjhhqwizpiywyrwjpgrg.supabase.co"
SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRqaGhxd2l6cGl5d3lyd2pwZ3JnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3OTY2NDAsImV4cCI6MjA3ODM3MjY0MH0.gEtg3ts6AAkrWZHIsWQxYzKAboZvRYyY5mlrZ5FMTtc"
BUCKET_NAME = "product-images"

# Variables para rastrear resultados
test_results = {
    'connection': False,
    'bucket_access': False,
    'public_url': False
}

print("=" * 70)
print("  DIAGNOSTICO DE SUPABASE STORAGE")
print("=" * 70)

try:
    # Crear cliente
    supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
    print_success("Cliente creado exitosamente")
    test_results['connection'] = True
    print()
    
    # Test 1: Listar todos los buckets
    print("TEST 1: Listando TODOS los buckets...")
    print("-" * 70)
    try:
        buckets = supabase.storage.list_buckets()
        print_info(f"Total de buckets: {len(buckets)}")
        
        if len(buckets) == 0:
            print_warning("No se detectaron buckets (puede ser problema de permisos)")
        else:
            for idx, bucket in enumerate(buckets, 1):
                print(f"\n   Bucket #{idx}:")
                print(f"   - ID: {bucket.id}")
                print(f"   - Nombre: {bucket.name}")
                print(f"   - Publico: {bucket.public}")
                print(f"   - Creado: {bucket.created_at}")
                
    except Exception as e:
        print_error(f"Error al listar buckets: {e}")
        print(f"   Tipo de error: {type(e).__name__}")
    
    # Test 2: Intentar acceder directamente al bucket
    print("\nTEST 2: Intentando acceder directamente al bucket '{}'...".format(BUCKET_NAME))
    print("-" * 70)
    try:
        # Intentar listar archivos en la raíz del bucket
        files = supabase.storage.from_(BUCKET_NAME).list()
        print_success("Acceso exitoso al bucket!")
        print_info(f"Archivos/carpetas en la raiz: {len(files)}")
        test_results['bucket_access'] = True
        
        if len(files) > 0:
            for file in files[:5]:  # Mostrar solo los primeros 5
                print(f"   - {file.get('name', 'N/A')}")
        else:
            print("   (El bucket esta vacio)")
            
    except Exception as e:
        print_error(f"Error al acceder al bucket: {e}")
        print(f"   Tipo de error: {type(e).__name__}")
    
    # Test 3: Intentar obtener una URL pública
    print("\nTEST 3: Intentando generar URL publica de prueba...")
    print("-" * 70)
    try:
        test_path = "test/prueba.jpg"
        public_url = supabase.storage.from_(BUCKET_NAME).get_public_url(test_path)
        print_success("URL generada (aunque el archivo no exista):")
        print(f"   {public_url}")
        test_results['public_url'] = True
        
    except Exception as e:
        print_error(f"Error al generar URL publica: {e}")
        print(f"   Tipo de error: {type(e).__name__}")
    
    # Test 4: Verificar la estructura del cliente
    print("\nTEST 4: Verificando configuracion del cliente...")
    print("-" * 70)
    try:
        print(f"   URL base: {SUPABASE_URL}")
        print(f"   Storage URL: {SUPABASE_URL}/storage/v1")
        print(f"   Bucket configurado: {BUCKET_NAME}")
    except Exception as e:
        print_error(f"Error: {e}")
    
    # Resumen basado en resultados
    print("\n" + "=" * 70)
    print(f"{Colors.BOLD}  RESUMEN DE DIAGNOSTICO{Colors.RESET}")
    print("=" * 70)
    print()
    
    # Estado de conexión
    if test_results['connection']:
        print_success("Conexion a Supabase establecida correctamente")
    else:
        print_error("No se pudo establecer conexion con Supabase")
    
    # Estado de acceso al bucket
    if test_results['bucket_access']:
        print_success(f"El bucket '{BUCKET_NAME}' existe y es accesible")
    else:
        print_error(f"El bucket '{BUCKET_NAME}' no existe o no es accesible")
    
    # Estado de URLs públicas
    if test_results['public_url']:
        print_success("Capacidad de generar URLs publicas: Funcional")
    else:
        print_error("No se pueden generar URLs publicas")
    
    print()
    print("-" * 70)
    
    # Diagnóstico final
    if all(test_results.values()):
        print()
        print(f"{Colors.GREEN}{Colors.BOLD}DIAGNOSTICO: TODO FUNCIONA CORRECTAMENTE{Colors.RESET}")
        print_info("El bucket esta configurado y listo para usar")
    elif test_results['bucket_access']:
        print()
        print(f"{Colors.YELLOW}{Colors.BOLD}DIAGNOSTICO: CONFIGURACION PARCIAL{Colors.RESET}")
        print_warning("El bucket funciona pero hay algunas limitaciones")
    else:
        print()
        print(f"{Colors.RED}{Colors.BOLD}DIAGNOSTICO: PROBLEMAS DETECTADOS{Colors.RESET}")
        print_error("Hay problemas con la configuracion del bucket")
        print()
        print_info("Soluciones sugeridas:")
        print("   1. Verificar que el bucket sea 'Public' en el dashboard")
        print("   2. Configurar politicas RLS para permitir acceso anonimo")
        print("   3. Revisar la configuracion de Storage en Supabase")
    
except Exception as e:
    print()
    print_error(f"ERROR CRITICO: {e}")
    print(f"   Tipo: {type(e).__name__}")

print("\n" + "=" * 70 + "\n")
