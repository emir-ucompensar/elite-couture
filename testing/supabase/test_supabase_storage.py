"""
Script de Testing para Supabase Storage
Elite Couture - Testing de bucket product-images

Este script prueba:
1. Conexión con Supabase
2. Subida de imágenes al bucket
3. Listado de archivos
4. Descarga de imágenes
5. Eliminación de archivos de prueba

Requisitos:
    pip install supabase Pillow python-dotenv

Uso:
    python test_supabase_storage.py
"""

import os
import sys
from pathlib import Path
from datetime import datetime
from io import BytesIO
from PIL import Image
import time

try:
    from supabase import create_client, Client
except ImportError:
    print("Error: Librería 'supabase' no instalada")
    print("   Instala con: pip install supabase")
    sys.exit(1)

try:
    from PIL import Image
except ImportError:
    print("Error: Librería 'Pillow' no instalada")
    print("   Instala con: pip install Pillow")
    sys.exit(1)


# =============================================================================
# CONFIGURACIÓN
# =============================================================================

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

# Credenciales de Supabase (las mismas del proyecto Android)
SUPABASE_URL = "https://tjhhqwizpiywyrwjpgrg.supabase.co"
SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRqaGhxd2l6cGl5d3lyd2pwZ3JnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3OTY2NDAsImV4cCI6MjA3ODM3MjY0MH0.gEtg3ts6AAkrWZHIsWQxYzKAboZvRYyY5mlrZ5FMTtc"

# Nombre del bucket
BUCKET_NAME = "product-images"

# Carpeta de prueba
TEST_FOLDER = "testing/"


# =============================================================================
# FUNCIONES DE UTILIDAD
# =============================================================================

def print_header(text):
    """Imprime un encabezado decorado"""
    print("\n" + "=" * 70)
    print(f"{Colors.BOLD}  {text}{Colors.RESET}")
    print("=" * 70)


def print_success(text):
    """Imprime mensaje de éxito"""
    print(f"{Colors.GREEN}[PASSED]{Colors.RESET} {text}")


def print_error(text):
    """Imprime mensaje de error"""
    print(f"{Colors.RED}[FAILED]{Colors.RESET} {text}")


def print_info(text):
    """Imprime mensaje informativo"""
    print(f"{Colors.CYAN}[INFO]{Colors.RESET} {text}")


def print_warning(text):
    """Imprime mensaje de advertencia"""
    print(f"{Colors.YELLOW}[WARN]{Colors.RESET} {text}")
    print(f"Alert {text}")


def create_test_image():
    """Crea una imagen de prueba en memoria"""
    img = Image.new('RGB', (800, 600), color=(73, 109, 137))
    
    # Añadir texto simulado (simplificado, sin texto real)
    # En producción podrías usar ImageDraw para añadir texto
    
    buffer = BytesIO()
    img.save(buffer, format='JPEG')
    buffer.seek(0)
    return buffer


# =============================================================================
# TESTS
# =============================================================================

def test_connection(supabase: Client):
    """Test 1: Probar conexión con Supabase"""
    print_header("TEST 1: Conexión con Supabase")
    
    try:
        # Intentar acceder directamente al bucket
        # (list_buckets requiere permisos especiales que anon key no tiene)
        print_info(f"Verificando acceso al bucket '{BUCKET_NAME}'...")
        
        files = supabase.storage.from_(BUCKET_NAME).list()
        
        print_success(f"Conexión establecida correctamente")
        print_success(f"Bucket '{BUCKET_NAME}' es accesible")
        print_info(f"Archivos/carpetas en la raíz: {len(files)}")
        
        # Verificar que podemos generar URLs públicas
        test_url = supabase.storage.from_(BUCKET_NAME).get_public_url("test.jpg")
        if test_url:
            print_success(f"Capacidad de generar URLs públicas: OK")
        
        return True
            
    except Exception as e:
        print_error(f"Error de conexión: {str(e)}")
        print_error(f"El bucket '{BUCKET_NAME}' no existe o no es accesible")
        return False


def test_upload_image(supabase: Client):
    """Test 2: Subir imagen de prueba"""
    print_header("TEST 2: Subir Imagen")
    
    try:
        # Crear imagen de prueba
        print_info("Creando imagen de prueba...")
        image_buffer = create_test_image()
        
        # Nombre único para el archivo
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{TEST_FOLDER}test_image_{timestamp}.jpg"
        
        print_info(f"Subiendo imagen: {filename}")
        
        # Subir archivo
        response = supabase.storage.from_(BUCKET_NAME).upload(
            filename,
            image_buffer.read(),
            file_options={"content-type": "image/jpeg"}
        )
        
        print_success(f"Imagen subida exitosamente")
        print_info(f"Path: {filename}")
        
        # Obtener URL pública
        public_url = supabase.storage.from_(BUCKET_NAME).get_public_url(filename)
        print_success(f"URL pública generada:")
        print(f"   {public_url}")
        
        return filename
        
    except Exception as e:
        print_error(f"Error al subir imagen: {str(e)}")
        return None


def test_list_files(supabase: Client, folder: str = None):
    """Test 3: Listar archivos en el bucket"""
    print_header("TEST 3: Listar Archivos")
    
    try:
        path = folder if folder else ""
        print_info(f"Listando archivos en: '{path if path else 'raíz'}'")
        
        files = supabase.storage.from_(BUCKET_NAME).list(path)
        
        if not files:
            print_warning("No se encontraron archivos")
            return []
        
        print_success(f"Se encontraron {len(files)} elementos")
        
        for idx, file in enumerate(files, 1):
            file_name = file.get('name', 'N/A')
            file_size = file.get('metadata', {}).get('size', 0)
            file_type = "Carpeta" if file.get('id') is None else "Archivo"
            
            size_kb = file_size / 1024 if file_size else 0
            print(f"   {idx}. {file_type} {file_name} ({size_kb:.2f} KB)")
        
        return files
        
    except Exception as e:
        print_error(f"Error al listar archivos: {str(e)}")
        return []


def test_download_image(supabase: Client, filename: str):
    """Test 4: Descargar imagen"""
    print_header("TEST 4: Descargar Imagen")
    
    try:
        print_info(f"Descargando: {filename}")
        
        # Descargar archivo
        data = supabase.storage.from_(BUCKET_NAME).download(filename)
        
        if data:
            size_kb = len(data) / 1024
            print_success(f"Imagen descargada exitosamente")
            print_info(f"Tamaño: {size_kb:.2f} KB")
            
            # Verificar que es una imagen válida
            try:
                img = Image.open(BytesIO(data))
                print_success(f"Imagen válida: {img.size[0]}x{img.size[1]} px")
                return True
            except Exception as e:
                print_warning(f"Los datos descargados no son una imagen válida: {e}")
                return False
        else:
            print_error("No se pudo descargar la imagen")
            return False
            
    except Exception as e:
        print_error(f"Error al descargar imagen: {str(e)}")
        return False


def test_get_public_url(supabase: Client, filename: str):
    """Test 5: Obtener URL pública"""
    print_header("TEST 5: URL Pública")
    
    try:
        print_info(f"Obteniendo URL pública de: {filename}")
        
        public_url = supabase.storage.from_(BUCKET_NAME).get_public_url(filename)
        
        print_success("URL pública generada:")
        print(f"   {public_url}")
        print_info("Copia esta URL en un navegador para verificar la imagen")
        
        return public_url
        
    except Exception as e:
        print_error(f"Error al obtener URL pública: {str(e)}")
        return None


def test_delete_file(supabase: Client, filename: str):
    """Test 6: Eliminar archivo de prueba"""
    print_header("TEST 6: Eliminar Archivo")
    
    try:
        print_info(f"Eliminando: {filename}")
        
        response = supabase.storage.from_(BUCKET_NAME).remove([filename])
        
        print_success("Archivo eliminado exitosamente")
        return True
        
    except Exception as e:
        print_error(f"Error al eliminar archivo: {str(e)}")
        return False


def test_upload_multiple_images(supabase: Client, count: int = 3):
    """Test 7: Subir múltiples imágenes"""
    print_header(f"TEST 7: Subir Múltiples Imágenes ({count})")
    
    uploaded_files = []
    
    for i in range(count):
        try:
            print_info(f"Subiendo imagen {i+1}/{count}...")
            
            image_buffer = create_test_image()
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"{TEST_FOLDER}test_batch_{timestamp}_{i}.jpg"
            
            supabase.storage.from_(BUCKET_NAME).upload(
                filename,
                image_buffer.read(),
                file_options={"content-type": "image/jpeg"}
            )
            
            uploaded_files.append(filename)
            print_success(f"Imagen {i+1} subida: {filename}")
            
            # Pequeña pausa para evitar nombres duplicados
            time.sleep(0.1)
            
        except Exception as e:
            print_error(f"Error en imagen {i+1}: {str(e)}")
    
    print_success(f"Total subidas: {len(uploaded_files)}/{count}")
    return uploaded_files


def cleanup_test_files(supabase: Client, files: list):
    """Limpiar archivos de prueba"""
    print_header("LIMPIEZA: Eliminando archivos de prueba")
    
    if not files:
        print_info("No hay archivos para limpiar")
        return
    
    try:
        print_info(f"Eliminando {len(files)} archivos...")
        response = supabase.storage.from_(BUCKET_NAME).remove(files)
        print_success(f"Archivos eliminados exitosamente")
    except Exception as e:
        print_error(f"Error al limpiar archivos: {str(e)}")


# =============================================================================
# FUNCIÓN PRINCIPAL
# =============================================================================

def main():
    """Ejecuta todos los tests"""
    print("\n" + "=" * 70)
    print(f"{Colors.BOLD}  TESTING DE SUPABASE STORAGE - ELITE COUTURE{Colors.RESET}")
    print("=" * 70)
    print(f"\n{Colors.CYAN}Bucket:{Colors.RESET} {BUCKET_NAME}")
    print(f"{Colors.CYAN}URL:{Colors.RESET} {SUPABASE_URL}")
    print(f"{Colors.CYAN}Fecha:{Colors.RESET} {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # Variables para rastrear resultados
    test_results = {
        'connection': False,
        'upload': False,
        'list': False,
        'download': False,
        'public_url': False,
        'batch_upload': False
    }
    
    # Crear cliente de Supabase
    try:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_ANON_KEY)
        print_success("Cliente de Supabase creado")
    except Exception as e:
        print_error(f"No se pudo crear el cliente: {str(e)}")
        return
    
    # Lista para rastrear archivos de prueba
    test_files = []
    
    # Ejecutar tests
    try:
        # Test 1: Conexión
        if test_connection(supabase):
            test_results['connection'] = True
        else:
            print_error("No se puede continuar sin conexión al bucket")
            return
        
        # Test 2: Subir imagen única
        filename = test_upload_image(supabase)
        if filename:
            test_files.append(filename)
            test_results['upload'] = True
        
        # Test 3: Listar archivos
        files = test_list_files(supabase, TEST_FOLDER)
        if files:
            test_results['list'] = True
        
        # Test 4: Descargar imagen
        if filename:
            if test_download_image(supabase, filename):
                test_results['download'] = True
        
        # Test 5: Obtener URL pública
        if filename:
            if test_get_public_url(supabase, filename):
                test_results['public_url'] = True
        
        # Test 6: Subir múltiples imágenes
        batch_files = test_upload_multiple_images(supabase, 3)
        test_files.extend(batch_files)
        if len(batch_files) > 0:
            test_results['batch_upload'] = True
        
        # Test 7: Listar nuevamente para ver todos los archivos
        test_list_files(supabase, TEST_FOLDER)
        if filename:
            test_download_image(supabase, filename)
        # Test 7: Listar nuevamente para ver todos los archivos
        test_list_files(supabase, TEST_FOLDER)
        
    except KeyboardInterrupt:
        print(f"\n\n{Colors.YELLOW}Tests interrumpidos por el usuario{Colors.RESET}")
    except Exception as e:
        print_error(f"Error inesperado: {str(e)}")
    finally:
        # Limpieza
        if test_files:
            print("\n")
            response = input("¿Deseas eliminar los archivos de prueba? (s/n): ")
            if response.lower() in ['s', 'y', 'si', 'yes']:
                cleanup_test_files(supabase, test_files)
            else:
                print_info("Archivos de prueba conservados")
                print_info(f"Archivos: {test_files}")
        
        # Resumen final
        print_header("RESUMEN DE TESTS")
        
        # Mostrar resultados con colores
        total_tests = len(test_results)
        passed_tests = sum(test_results.values())
        failed_tests = total_tests - passed_tests
        
        print(f"\nTests ejecutados: {total_tests}")
        print(f"{Colors.GREEN}Exitosos: {passed_tests}{Colors.RESET}")
        print(f"{Colors.RED}Fallidos: {failed_tests}{Colors.RESET}")
        print()
        
        # Detalle de cada test
        for test_name, result in test_results.items():
            status = f"{Colors.GREEN}[PASSED]{Colors.RESET}" if result else f"{Colors.RED}[FAILED]{Colors.RESET}"
            test_display = test_name.replace('_', ' ').title()
            print(f"  {status} {test_display}")
        
        print()
        if passed_tests == total_tests:
            print(f"{Colors.GREEN}{Colors.BOLD}RESULTADO: TODOS LOS TESTS PASARON{Colors.RESET}")
        elif passed_tests > 0:
            print(f"{Colors.YELLOW}{Colors.BOLD}RESULTADO: ALGUNOS TESTS FALLARON{Colors.RESET}")
        else:
            print(f"{Colors.RED}{Colors.BOLD}RESULTADO: TODOS LOS TESTS FALLARON{Colors.RESET}")
    
    # Mensaje final
    print_header("TESTS COMPLETADOS")
    print_success("Todos los tests han finalizado")
    print_info(f"Revisa los resultados arriba para ver el estado de cada test")
    print("\n" + "=" * 70 + "\n")


if __name__ == "__main__":
    main()
