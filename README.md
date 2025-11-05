# 👗 Elite Couture — Tienda de Moda
## Proyecto Académico Android

**Elite Couture** es una aplicación móvil académica desarrollada para Android, que simula el funcionamiento de una tienda de ropa moderna.  
El proyecto tiene como objetivo aplicar principios de desarrollo móvil nativo con **Android SDK y Gradle**, integrando posteriormente funciones de **registro, login y gestión CRUD de productos**.

---

## Objetivos del Proyecto

- Desarrollar una **aplicación Android nativa** desde cero sin utilizar Android Studio (solo Gradle + VS Code).
- Implementar gradualmente un sistema **CRUD** para administrar prendas, usuarios y pedidos.
- Diseñar una interfaz **intuitiva, moderna y ligera**, adaptable a dispositivos Android 10 en adelante.
- Aplicar buenas prácticas de arquitectura y modularización en proyectos Android.

---

## Tecnologías Utilizadas

| Categoría | Tecnología / Herramienta |
|------------|---------------------------|
| Lenguaje principal | Kotlin |
| Build system | Gradle |
| Min SDK | Android 10 (API 29) |
| Target SDK | Android 34 |
| IDE utilizado | Visual Studio Code |
| Control de versiones | Git + GitHub |

---

## Cómo Clonar y Compilar el Proyecto

### Clonar el repositorio

```bash
git clone https://github.com/emir-ucompensar/elite-couture.git
cd EliteCouture
````

### (Opcional) Descargar desde *Releases*

Si prefieres, puedes descargar el código fuente directamente desde la sección **[Releases](https://github.com/emir-ucompensar/elite-couture/releases)** del repositorio.

### Construir la aplicación

Compila el APK de depuración con el siguiente comando:

```bash
gradle assembleDebug
```

Esto generará el archivo:

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## Cómo Instalar y Probar el APK

1. Inicia tu **Emulador Android** (o conecta un dispositivo físico con depuración USB habilitada).
2. Instala el APK generado:

   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
3. ¡Listo! La app estará disponible en el menú de aplicaciones bajo el nombre **Elite Couture** 👗

---

## Funcionalidades Implementadas

| Funcionalidad | Estado | Descripción |
|--------------|--------|-------------|
| 🧍‍♀️ Autenticación | ✅ | Sistema completo de login, registro y modo invitado |
| 👤 Perfil de usuario | ✅ | Edición completa con validación y cifrado de dirección |
| 🎨 Sistema de diálogos | ✅ | Diálogos personalizados con identidad visual de marca |
| �️ Modo invitado | ✅ | Acceso limitado con restricciones visuales |
| 🔐 Seguridad | ✅ | Encriptación AES-256-CBC para datos sensibles |
| 👚 Gestión CRUD | 🚧 | En desarrollo |
| 🛍️ Carrito de compras | 🚧 | En desarrollo |
| 🎯 Catálogo visual | 🚧 | Filtros por género y categoría |

---

## 🎨 Sistema de Diálogos Personalizados

Elite Couture cuenta con un sistema unificado de diálogos (`EliteCoutureDialog`) que mantiene la identidad visual en toda la app:

### Características
- ✨ **Tipografía Elite Sans Semibold** en todos los botones
- � **Texto en MAYÚSCULAS** con letter-spacing optimizado (0.05)
- 🔗 **Subrayado en botones** para mejor accesibilidad
- 📏 **Tamaño profesional** (12sp) compacto y legible
- 🎨 **Color de marca consistente** (#560E2D)
- ⚙️ **Soporte para 3 botones** (Positivo/Neutral/Negativo)

### Ejemplo de uso
```kotlin
EliteCoutureDialog.create(requireContext())
    .setTitle(R.string.dialog_title)
    .setMessage(R.string.dialog_message)
    .setPositiveButton(R.string.button_save) {
        saveChanges()
    }
    .setNeutralButton(R.string.button_discard) {
        discardChanges()
    }
    .setNegativeButton(R.string.button_cancel)
    .show()
```

📖 **Documentación completa**: Ver [DIALOG_SYSTEM.md](design/DIALOG_SYSTEM.md)

---

## Créditos y Licencia

Proyecto académico desarrollado por **Emir en UCompensar**
Licencia: 
MIT — libre para uso educativo.

---

> *“Elite Couture no es solo moda, es el arte de compilar estilo.”*
