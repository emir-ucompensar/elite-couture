#!/usr/bin/env python3
"""
Script para verificar el estado de la tabla de favoritos en la base de datos
Consulta directamente la base de datos SQLite de Elite Couture
"""

import subprocess
import sys
from datetime import datetime

# Colores ANSI
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    WHITE = '\033[97m'
    RED = '\033[91m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header(text, color=Colors.CYAN):
    """Imprime un encabezado con estilo"""
    print(f"\n{color}{Colors.BOLD}{'='*60}")
    print(f"  {text}")
    print(f"{'='*60}{Colors.RESET}\n")

def run_adb_command(command):
    """Ejecuta un comando ADB y retorna el resultado"""
    try:
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            check=True
        )
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        return None

def check_adb_connection():
    """Verifica que ADB esté conectado"""
    result = run_adb_command("adb devices")
    if result and "device" in result and result.count('\n') > 1:
        return True
    return False

def query_database(query, description):
    """Ejecuta una query en la base de datos de la app"""
    db_path = "/data/data/com.elitecouture.app/databases/elite_couture.db"
    
    # Intentar con shell normal primero
    cmd = f'adb shell "sqlite3 {db_path} \\"{query}\\""'
    result = run_adb_command(cmd)
    
    # Si falla, intentar con su (requiere root)
    if result is None or "Error" in result:
        cmd = f'adb shell "su -c \'sqlite3 {db_path} \\"{query}\\"\'"'
        result = run_adb_command(cmd)
    
    if result is None:
        print(f"{Colors.RED}Error ejecutando query: {description}{Colors.RESET}")
        return None
    
    return result

def show_favorites_table():
    """Muestra el contenido de la tabla favorites con JOIN a products"""
    print_header("Contenido de la Tabla FAVORITES", Colors.CYAN)
    
    query = """
    SELECT 
        f.id,
        f.user_uuid,
        f.product_uuid,
        p.name as product_name,
        datetime(f.created_at/1000, 'unixepoch') as created_at
    FROM favorites f
    LEFT JOIN products p ON f.product_uuid = p.uuid
    ORDER BY f.created_at DESC;
    """
    
    result = query_database(query, "Consultar favoritos")
    
    if result:
        if result.strip():
            # Imprimir encabezados
            print(f"{Colors.BOLD}{Colors.GREEN}ID | User UUID | Product UUID | Product Name | Created At{Colors.RESET}")
            print(f"{Colors.WHITE}{'-'*90}{Colors.RESET}")
            
            # Imprimir resultados con formato
            for line in result.split('\n'):
                if line.strip():
                    parts = line.split('|')
                    if len(parts) >= 5:
                        fav_id = parts[0]
                        user_uuid = parts[1][:8] + "..."  # Truncar para mejor lectura
                        prod_uuid = parts[2][:8] + "..."
                        prod_name = parts[3]
                        created = parts[4]
                        print(f"{Colors.CYAN}{fav_id}{Colors.RESET} | "
                              f"{Colors.YELLOW}{user_uuid}{Colors.RESET} | "
                              f"{Colors.MAGENTA}{prod_uuid}{Colors.RESET} | "
                              f"{Colors.WHITE}{prod_name}{Colors.RESET} | "
                              f"{Colors.GREEN}{created}{Colors.RESET}")
                    else:
                        print(line)
        else:
            print(f"{Colors.YELLOW}La tabla de favoritos está vacía{Colors.RESET}")
    else:
        print(f"{Colors.RED}No se pudo consultar la base de datos.{Colors.RESET}")
        print(f"{Colors.YELLOW}Nota: Este script requiere:")
        print(f"  - Emulador (acceso directo a archivos)")
        print(f"  - O dispositivo rooteado con 'su' disponible{Colors.RESET}")

def show_favorites_count():
    """Muestra el conteo total de favoritos"""
    print_header("Estadísticas de Favoritos", Colors.GREEN)
    
    # Total de favoritos
    query = "SELECT COUNT(*) as total FROM favorites;"
    result = query_database(query, "Contar favoritos")
    
    if result:
        print(f"{Colors.BOLD}Total de favoritos:{Colors.RESET} {Colors.CYAN}{result}{Colors.RESET}")
    
    # Favoritos por usuario
    query = """
    SELECT 
        u.name as user_name,
        COUNT(f.id) as favorites_count
    FROM users u
    LEFT JOIN favorites f ON u.uuid = f.user_uuid
    GROUP BY u.uuid, u.name
    HAVING favorites_count > 0
    ORDER BY favorites_count DESC;
    """
    
    result = query_database(query, "Favoritos por usuario")
    
    if result and result.strip():
        print(f"\n{Colors.BOLD}Favoritos por usuario:{Colors.RESET}")
        for line in result.split('\n'):
            if line.strip():
                parts = line.split('|')
                if len(parts) == 2:
                    user_name = parts[0]
                    count = parts[1]
                    print(f"  {Colors.WHITE}{user_name}{Colors.RESET}: {Colors.CYAN}{count} favorito(s){Colors.RESET}")

def show_products_most_favorited():
    """Muestra los productos más añadidos a favoritos"""
    print_header("Productos Más Populares", Colors.MAGENTA)
    
    query = """
    SELECT 
        p.name as product_name,
        COUNT(f.id) as times_favorited
    FROM products p
    LEFT JOIN favorites f ON p.uuid = f.product_uuid
    GROUP BY p.uuid, p.name
    HAVING times_favorited > 0
    ORDER BY times_favorited DESC
    LIMIT 5;
    """
    
    result = query_database(query, "Productos más favoritos")
    
    if result and result.strip():
        print(f"{Colors.BOLD}Top 5 productos más favoritos:{Colors.RESET}\n")
        for i, line in enumerate(result.split('\n'), 1):
            if line.strip():
                parts = line.split('|')
                if len(parts) == 2:
                    prod_name = parts[0]
                    count = parts[1]
                    emoji = "🥇" if i == 1 else "🥈" if i == 2 else "🥉" if i == 3 else "  "
                    print(f"{emoji} {i}. {Colors.WHITE}{prod_name}{Colors.RESET}: {Colors.CYAN}{count} veces{Colors.RESET}")
    else:
        print(f"{Colors.YELLOW}⚠ No hay productos favoritos aún{Colors.RESET}")

def verify_database_integrity():
    """Verifica la integridad de la base de datos"""
    print_header("Verificación de Integridad", Colors.YELLOW)
    
    # Verificar Foreign Keys
    query = "PRAGMA foreign_keys;"
    result = query_database(query, "Verificar FK habilitadas")
    if result:
        fk_enabled = result == "1"
        status = f"{Colors.GREEN}✓ Habilitadas{Colors.RESET}" if fk_enabled else f"{Colors.RED}✗ Deshabilitadas{Colors.RESET}"
        print(f"Foreign Keys: {status}")
    
    # Verificar restricción UNIQUE
    query = """
    SELECT user_uuid, product_uuid, COUNT(*) as duplicates
    FROM favorites
    GROUP BY user_uuid, product_uuid
    HAVING duplicates > 1;
    """
    result = query_database(query, "Verificar duplicados")
    
    if result and result.strip():
        print(f"{Colors.RED}Se encontraron duplicados (violación UNIQUE):{Colors.RESET}")
        print(result)
    else:
        print(f"{Colors.GREEN}No hay duplicados (restricción UNIQUE funciona){Colors.RESET}")

def main():
    """Función principal"""
    print_header("VERIFICAR BASE DE DATOS - Favoritos Elite Couture", Colors.CYAN)
    print(f"{Colors.WHITE}Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
    
    # Verificar conexión ADB
    if not check_adb_connection():
        print(f"{Colors.RED}Error: No se detectó ningún dispositivo conectado{Colors.RESET}")
        sys.exit(1)
    
    print(f"{Colors.GREEN}Dispositivo conectado{Colors.RESET}")
    
    # Mostrar tabla de favoritos
    show_favorites_table()
    
    # Mostrar estadísticas
    show_favorites_count()
    
    # Productos más populares
    show_products_most_favorited()
    
    # Verificar integridad
    verify_database_integrity()
    
    print(f"\n{Colors.GREEN}✓ Verificación completada{Colors.RESET}\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n\n{Colors.YELLOW}Script interrumpido por el usuario{Colors.RESET}")
        sys.exit(0)
    except Exception as e:
        print(f"\n{Colors.RED}Error inesperado: {e}{Colors.RESET}")
        sys.exit(1)
