#!/usr/bin/env python3
"""
Script para probar la funcionalidad de Favoritos en Elite Couture
Ejecuta este script después de instalar la APK para monitorear los logs en tiempo real
"""

import subprocess
import sys
import time
from datetime import datetime

# Colores ANSI para terminal
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    WHITE = '\033[97m'
    MAGENTA = '\033[95m'
    RED = '\033[91m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_header(text, color=Colors.CYAN):
    """Imprime un encabezado con estilo"""
    print(f"\n{color}{Colors.BOLD}{'='*50}")
    print(f"  {text}")
    print(f"{'='*50}{Colors.RESET}\n")

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
        print(f"{Colors.RED}Error ejecutando comando: {e}{Colors.RESET}")
        return None

def clear_logcat():
    """Limpia el buffer de logcat"""
    print(f"{Colors.YELLOW}1. Limpiando logcat...{Colors.RESET}")
    run_adb_command("adb logcat -c")
    time.sleep(0.5)

def start_app():
    """Inicia la aplicación Elite Couture"""
    print(f"{Colors.YELLOW}2. Iniciando la app Elite Couture...{Colors.RESET}")
    run_adb_command("adb shell am start -n com.elitecouture.app/.ui.MainActivity")
    time.sleep(1)

def show_instructions():
    """Muestra las instrucciones de prueba"""
    print_header("INSTRUCCIONES DE PRUEBA", Colors.GREEN)
    print(f"{Colors.WHITE}1. Inicia sesión con cualquier usuario")
    print(f"2. Haz clic en la estrella de algunos productos")
    print(f"3. Añade y quita favoritos varias veces")
    print(f"4. Observa los logs en tiempo real abajo{Colors.RESET}")
    print(f"\n{Colors.MAGENTA}Tip: Presiona Ctrl+C para detener el monitoreo{Colors.RESET}\n")

def monitor_logs():
    """Monitorea los logs de favoritos en tiempo real"""
    print_header("LOGS EN TIEMPO REAL", Colors.CYAN)
    
    # Tags a monitorear
    tags = [
        "FavoriteDao:D",
        "AddToFavoritesUseCase:D",
        "RemoveFromFavoritesUC:D",
        "ProductListAdapter:D"
    ]
    
    # Comando logcat con filtros
    logcat_cmd = f"adb logcat {' '.join(tags)} *:S"
    
    try:
        # Ejecutar logcat en modo streaming
        process = subprocess.Popen(
            logcat_cmd,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1
        )
        
        # Leer líneas en tiempo real
        for line in process.stdout:
            line = line.strip()
            if line:
                # Colorear según el tag
                if "FavoriteDao" in line:
                    print(f"{Colors.CYAN}{line}{Colors.RESET}")
                elif "AddToFavoritesUseCase" in line:
                    print(f"{Colors.GREEN}{line}{Colors.RESET}")
                elif "RemoveFromFavoritesUC" in line:
                    print(f"{Colors.YELLOW}{line}{Colors.RESET}")
                elif "ProductListAdapter" in line:
                    print(f"{Colors.MAGENTA}{line}{Colors.RESET}")
                else:
                    print(line)
                    
    except KeyboardInterrupt:
        print(f"\n\n{Colors.GREEN}✓ Monitoreo detenido por el usuario{Colors.RESET}")
        process.terminate()
    except Exception as e:
        print(f"{Colors.RED}Error monitoreando logs: {e}{Colors.RESET}")
        sys.exit(1)

def check_adb_connection():
    """Verifica que ADB esté conectado a un dispositivo"""
    result = run_adb_command("adb devices")
    if result and "device" in result and result.count('\n') > 1:
        return True
    return False

def main():
    """Función principal"""
    print_header("TEST: Funcionalidad de Favoritos - Elite Couture", Colors.CYAN)
    print(f"{Colors.WHITE}Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}{Colors.RESET}")
    
    # Verificar conexión ADB
    if not check_adb_connection():
        print(f"{Colors.RED}Error: No se detectó ningún dispositivo conectado{Colors.RESET}")
        print(f"{Colors.YELLOW}Asegúrate de que:")
        print(f"  - El emulador/dispositivo está encendido")
        print(f"  - ADB está instalado y en el PATH")
        print(f"  - USB debugging está habilitado{Colors.RESET}")
        sys.exit(1)
    
    print(f"{Colors.GREEN}✓ Dispositivo conectado{Colors.RESET}")
    
    # Limpiar logcat
    clear_logcat()
    
    # Iniciar app
    start_app()
    
    # Mostrar instrucciones
    show_instructions()
    
    # Monitorear logs
    monitor_logs()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n\n{Colors.GREEN}Script terminado{Colors.RESET}")
        sys.exit(0)
