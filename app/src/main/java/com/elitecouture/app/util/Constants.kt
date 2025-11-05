package com.elitecouture.app.util

/**
 * Objeto centralizado que contiene todas las constantes de la aplicación.
 * 
 * Organizado por categorías para facilitar el acceso y mantenimiento.
 * Sigue el principio DRY (Don't Repeat Yourself) eliminando valores hardcodeados
 * dispersos por el código.
 */
object Constants {

    /**
     * Constantes relacionadas con la autenticación de usuarios
     */
    object Auth {
        /** Longitud mínima requerida para contraseñas */
        const val MIN_PASSWORD_LENGTH = 6
        
        /** Longitud máxima permitida para contraseñas */
        const val MAX_PASSWORD_LENGTH = 128
        
        /** Longitud mínima para nombres de usuario */
        const val MIN_NAME_LENGTH = 2
        
        /** Longitud máxima para nombres de usuario */
        const val MAX_NAME_LENGTH = 50
        
        /** Patrón regex para validación de email */
        const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        
        /** Tiempo de sesión antes de logout automático (milisegundos) */
        const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutos
    }

    /**
     * Constantes relacionadas con la sesión de usuario
     */
    object Session {
        /** Clave para almacenar el ID de usuario en SharedPreferences */
        const val KEY_USER_ID = "user_id"
        
        /** Clave para almacenar el email en SharedPreferences */
        const val KEY_USER_EMAIL = "user_email"
        
        /** Clave para almacenar el nombre en SharedPreferences */
        const val KEY_USER_NAME = "user_name"
        
        /** Clave para identificar modo invitado */
        const val KEY_IS_GUEST = "is_guest"
        
        /** Clave para saber si es primer inicio */
        const val KEY_FIRST_TIME = "first_time_user"
        
        /** Nombre del archivo SharedPreferences */
        const val PREFERENCES_NAME = "elite_couture_prefs"
    }

    /**
     * Constantes relacionadas con la base de datos local
     */
    object Database {
        /** Nombre de la base de datos Room */
        const val DATABASE_NAME = "elite_couture_database"
        
        /** Versión actual de la base de datos */
        const val DATABASE_VERSION = 1
        
        /** Nombre de la tabla de usuarios */
        const val TABLE_USERS = "users"
        
        /** Nombre de la tabla de productos */
        const val TABLE_PRODUCTS = "products"
        
        /** Nombre de la tabla de categorías */
        const val TABLE_CATEGORIES = "categories"
    }

    /**
     * Constantes relacionadas con productos y tienda
     */
    object Store {
        /** Filtro para mostrar todos los productos */
        const val FILTER_ALL_PRODUCTS = "all"
        
        /** Filtro de género: Hombre */
        const val GENDER_MEN = "men"
        
        /** Filtro de género: Mujer */
        const val GENDER_WOMEN = "women"
        
        /** Categoría: Pantalones */
        const val CATEGORY_PANTS = "pants"
        
        /** Categoría: Chaquetas */
        const val CATEGORY_JACKETS = "jackets"
        
        /** Categoría: Abrigos */
        const val CATEGORY_COATS = "coats"
        
        /** Categoría: Faldas */
        const val CATEGORY_SKIRTS = "skirts"
        
        /** Categoría: Accesorios */
        const val CATEGORY_ACCESSORIES = "accessories"
        
        /** Límite de productos por página */
        const val PRODUCTS_PER_PAGE = 20
        
        /** Tiempo de cache para productos (milisegundos) */
        const val PRODUCT_CACHE_DURATION_MS = 5 * 60 * 1000L // 5 minutos
    }

    /**
     * Constantes de UI y animaciones
     */
    object UI {
        /** Duración de splash screen (milisegundos) */
        const val SPLASH_DURATION_MS = 2000L
        
        /** Duración de animaciones estándar (milisegundos) */
        const val ANIMATION_DURATION_MS = 300L
        
        /** Duración de animaciones largas (milisegundos) */
        const val ANIMATION_DURATION_LONG_MS = 500L
        
        /** Delay para mostrar mensajes Toast (milisegundos) */
        const val TOAST_DURATION_SHORT = 2000
        
        /** Delay para mostrar mensajes Toast largos (milisegundos) */
        const val TOAST_DURATION_LONG = 3500
        
        /** Tamaño de avatar por defecto (dp) */
        const val DEFAULT_AVATAR_SIZE_DP = 80
    }

    /**
     * Constantes de navegación
     */
    object Navigation {
        /** ID de destino: Login */
        const val DEST_LOGIN = "login"
        
        /** ID de destino: Register */
        const val DEST_REGISTER = "register"
        
        /** ID de destino: Store */
        const val DEST_STORE = "store"
        
        /** ID de destino: Profile */
        const val DEST_PROFILE = "profile"
        
        /** ID de destino: Cart */
        const val DEST_CART = "cart"
        
        /** Key para pasar el ID de producto entre pantallas */
        const val ARG_PRODUCT_ID = "product_id"
        
        /** Key para pasar el filtro de categoría */
        const val ARG_CATEGORY_FILTER = "category_filter"
        
        /** Key para pasar el filtro de género */
        const val ARG_GENDER_FILTER = "gender_filter"
    }

    /**
     * Constantes de red y API (para uso futuro)
     */
    object Network {
        /** Timeout para conexiones (segundos) */
        const val CONNECT_TIMEOUT_SECONDS = 30L
        
        /** Timeout para lectura (segundos) */
        const val READ_TIMEOUT_SECONDS = 30L
        
        /** Timeout para escritura (segundos) */
        const val WRITE_TIMEOUT_SECONDS = 30L
        
        /** Número máximo de reintentos */
        const val MAX_RETRIES = 3
        
        /** URL base de la API (placeholder) */
        const val BASE_URL = "https://api.elitecouture.com/"
    }

    /**
     * Constantes de logging y debug
     */
    object Debug {
        /** Tag principal para logs */
        const val LOG_TAG = "EliteCouture"
        
        /** Habilitar logs en modo debug */
        const val ENABLE_LOGGING = true
        
        /** Habilitar logs detallados */
        const val ENABLE_VERBOSE_LOGGING = false
    }

    /**
     * Constantes de validación de entrada
     */
    object Validation {
        /** Caracteres permitidos en nombres */
        const val NAME_ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZáéíóúÁÉÍÓÚñÑ "
        
        /** Longitud máxima de comentarios */
        const val MAX_COMMENT_LENGTH = 500
        
        /** Longitud máxima de direcciones */
        const val MAX_ADDRESS_LENGTH = 200
    }

    /**
     * Códigos de resultado para Activities
     */
    object RequestCode {
        /** Código para solicitud de login */
        const val REQUEST_LOGIN = 1001
        
        /** Código para solicitud de selección de imagen */
        const val REQUEST_IMAGE_PICK = 1002
        
        /** Código para solicitud de permisos */
        const val REQUEST_PERMISSIONS = 1003
    }

    /**
     * Códigos de error personalizados
     */
    object ErrorCode {
        /** Error: Usuario no encontrado */
        const val ERROR_USER_NOT_FOUND = 404
        
        /** Error: Credenciales inválidas */
        const val ERROR_INVALID_CREDENTIALS = 401
        
        /** Error: Usuario ya existe */
        const val ERROR_USER_ALREADY_EXISTS = 409
        
        /** Error: Sesión expirada */
        const val ERROR_SESSION_EXPIRED = 440
        
        /** Error: Servidor no disponible */
        const val ERROR_SERVER_UNAVAILABLE = 503
    }
}
