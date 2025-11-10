#!/usr/bin/env python3
"""
Script de ayuda rápida para testing de Elite Couture
Muestra información del dispositivo y comandos útiles
"""

import subprocess
import sys

class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    WHITE = '\033[97m'
    RED = '\033[91m'
    MAGENTA = '\033[95m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def run_command(command):
    """Ejecuta un comando y retorna el resultado"""
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True, check=True)
        return result.stdout.strip()
    except:
        return None

def print_section(title):
    """Imprime un título de sección"""
    print(f"\n{Colors.CYAN}{Colors.BOLD}{title}{Colors.RESET}")
    print(f"{Colors.CYAN}{'─' * len(title)}{Colors.RESET}")

def check_device():
    """Verifica estado del dispositivo"""
    print_section("📱 Estado del Dispositivo")
    
    result = run_command("adb devices")
    if result:
        lines = result.split('\n')[1:]  # Skip header
        devices = [line for line in lines if line.strip() and 'device' in line]
        
        if devices:
            print(f"{Colors.GREEN}✓ Dispositivo(s) conectado(s):{Colors.RESET}")
            for device in devices:
                parts = device.split('\t')
                if len(parts) >= 2:
                    device_id = parts[0]
                    status = parts[1]
                    print(f"  • {Colors.WHITE}{device_id}{Colors.RESET} ({status})")
            return True
        else:
            print(f"{Colors.RED}✗ No hay dispositivos conectados{Colors.RESET}")
            return False
    else:
        print(f"{Colors.RED}✗ ADB no está disponible{Colors.RESET}")
        return False

def show_app_info():
    """Muestra información de la app"""
    print_section("📦 Información de la App")
    
    # Verificar si la app está instalada
    result = run_command("adb shell pm list packages | grep com.elitecouture.app")
    if result:
        print(f"{Colors.GREEN}✓ App instalada: {Colors.WHITE}com.elitecouture.app{Colors.RESET}")
    else:
        print(f"{Colors.RED}✗ App no instalada{Colors.RESET}")
        return
    
    # Versión de la app
    result = run_command("adb shell dumpsys package com.elitecouture.app | grep versionName")
    if result:
        version = result.split('=')[1] if '=' in result else "N/A"
        print(f"  Versión: {Colors.CYAN}{version}{Colors.RESET}")
    
    # Estado de la app
    result = run_command("adb shell pidof com.elitecouture.app")
    if result and result.strip():
        print(f"  Estado: {Colors.GREEN}En ejecución (PID: {result}){Colors.RESET}")
    else:
        print(f"  Estado: {Colors.YELLOW}Detenida{Colors.RESET}")

def show_database_info():
    """Muestra información de la base de datos"""
    print_section("🗄️ Base de Datos")
    
    db_path = "/data/data/com.elitecouture.app/databases/elite_couture.db"
    
    # Verificar si existe
    result = run_command(f'adb shell "ls {db_path} 2>/dev/null"')
    if result:
        print(f"{Colors.GREEN}✓ Base de datos encontrada{Colors.RESET}")
        
        # Tamaño
        size_result = run_command(f'adb shell "ls -lh {db_path} 2>/dev/null"')
        if size_result:
            parts = size_result.split()
            if len(parts) >= 5:
                size = parts[4]
                print(f"  Tamaño: {Colors.CYAN}{size}{Colors.RESET}")
    else:
        print(f"{Colors.YELLOW}⚠ Base de datos no encontrada (requiere acceso root){Colors.RESET}")

def show_available_scripts():
    """Muestra scripts disponibles"""
    print_section("🧪 Scripts de Testing Disponibles")
    
    scripts = [
        {
            "name": "test_favorites.py",
            "desc": "Monitorear logs de favoritos en tiempo real",
            "cmd": "python testing/test_favorites.py"
        },
        {
            "name": "check_favorites_db.py",
            "desc": "Consultar base de datos de favoritos",
            "cmd": "python testing/check_favorites_db.py"
        },
        {
            "name": "copy_product_images.py",
            "desc": "Copiar imágenes de productos al dispositivo",
            "cmd": "python testing/copy_product_images.py"
        }
    ]
    
    for i, script in enumerate(scripts, 1):
        print(f"\n{Colors.BOLD}{i}. {Colors.CYAN}{script['name']}{Colors.RESET}")
        print(f"   {Colors.WHITE}{script['desc']}{Colors.RESET}")
        print(f"   {Colors.YELLOW}$ {script['cmd']}{Colors.RESET}")

def show_useful_commands():
    """Muestra comandos útiles de ADB"""
    print_section("🔧 Comandos Útiles de ADB")
    
    commands = [
        ("Instalar APK", "adb install -r app/build/outputs/apk/debug/app-debug.apk"),
        ("Iniciar app", "adb shell am start -n com.elitecouture.app/.ui.MainActivity"),
        ("Detener app", "adb shell am force-stop com.elitecouture.app"),
        ("Limpiar datos", "adb shell pm clear com.elitecouture.app"),
        ("Ver logs", "adb logcat -s TAG:D"),
        ("Captura de pantalla", "adb exec-out screencap -p > screenshot.png"),
        ("Grabar pantalla", "adb shell screenrecord /sdcard/record.mp4"),
    ]
    
    for desc, cmd in commands:
        print(f"\n{Colors.WHITE}{desc}:{Colors.RESET}")
        print(f"  {Colors.YELLOW}$ {cmd}{Colors.RESET}")

def main():
    """Función principal"""
    print(f"\n{Colors.CYAN}{Colors.BOLD}{'='*60}")
    print(f"  🧪 Elite Couture - Testing Helper")
    print(f"{'='*60}{Colors.RESET}\n")
    
    # Verificar dispositivo
    device_ok = check_device()
    
    if device_ok:
        # Mostrar info de la app
        show_app_info()
        
        # Mostrar info de DB
        show_database_info()
    
    # Siempre mostrar scripts disponibles
    show_available_scripts()
    
    # Mostrar comandos útiles
    show_useful_commands()
    
    print(f"\n{Colors.GREEN}{'='*60}{Colors.RESET}")
    print(f"{Colors.WHITE}Para más información, consulta: {Colors.CYAN}testing/README.md{Colors.RESET}\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n{Colors.YELLOW}Interrumpido{Colors.RESET}")
        sys.exit(0)
