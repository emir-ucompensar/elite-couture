# 👗 Elite Couture — Tienda de Moda (Proyecto Académico Android)

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
| Lenguaje principal | Java |
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

## Funcionalidades Planeadas

| Etapa                  | Descripción                                |
| ---------------------- | ------------------------------------------ |
| 🧍‍♀️ Registro y login | Creación y autenticación de usuarios.      |
| 👚 Gestión CRUD        | Alta, baja y edición de prendas de ropa.   |
| 🛍️ Catálogo visual    | Listado dinámico de productos disponibles. |
| 💬 Notificaciones      | Recordatorios de stock y novedades.        |
| ⚙️ Configuración       | Preferencias y temas visuales.             |

---

## Créditos y Licencia

Proyecto académico desarrollado por **Emir en UCompensar**
Licencia: MIT — libre para uso educativo.

---

> *“Elite Couture no es solo moda, es el arte de compilar estilo.”*

```

---
