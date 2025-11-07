package com.elitecouture.app.data.local.contract

/**
 * Centralized definition of table and column names for the local SQLite schema.
 * Keeping the contract in one place makes migrations and queries easier to maintain.
 */
object DatabaseContract {
    const val DATABASE_NAME = "elite_couture.db"
    const val DATABASE_VERSION = 7 // Incrementado para añadir columna tags a products

    object Users {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_UUID = "uuid"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_IS_GUEST = "is_guest"
        const val COLUMN_CREATED_AT = "created_at"

        val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_UUID TEXT NOT NULL UNIQUE,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_IS_GUEST INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
    }

    object Products {
        const val TABLE_NAME = "products"
        const val COLUMN_ID = "id"
        const val COLUMN_UUID = "uuid"
        const val COLUMN_NAME = "name"
        const val COLUMN_TYPE = "type"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PRICE = "price"
        const val COLUMN_STOCK = "stock"
        const val COLUMN_IMAGES = "images" // Cambiado de image_url a images (string delimitado por '|')
        const val COLUMN_TAGS = "tags" // Tags delimitados por '|' (ej: "Vestido|Mujer|Elegante")
        const val COLUMN_IS_VISIBLE_TO_GUEST = "is_visible_to_guest"

        val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_UUID TEXT NOT NULL UNIQUE,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_TYPE TEXT,
                $COLUMN_GENDER TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_STOCK INTEGER NOT NULL DEFAULT 0,
                $COLUMN_IMAGES TEXT NOT NULL DEFAULT '',
                $COLUMN_TAGS TEXT NOT NULL DEFAULT '',
                $COLUMN_IS_VISIBLE_TO_GUEST INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
    }

    object CartItems {
        const val TABLE_NAME = "cart_items"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_UUID = "user_uuid"
        const val COLUMN_PRODUCT_UUID = "product_uuid"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_ADDED_AT = "added_at"

        val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_UUID TEXT NOT NULL,
                $COLUMN_PRODUCT_UUID TEXT NOT NULL,
                $COLUMN_QUANTITY INTEGER NOT NULL DEFAULT 1,
                $COLUMN_ADDED_AT INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_USER_UUID) REFERENCES ${Users.TABLE_NAME}(${Users.COLUMN_UUID}) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_PRODUCT_UUID) REFERENCES ${Products.TABLE_NAME}(${Products.COLUMN_UUID}) ON DELETE CASCADE,
                UNIQUE ($COLUMN_USER_UUID, $COLUMN_PRODUCT_UUID)
            )
        """.trimIndent()
    }

    object Favorites {
        const val TABLE_NAME = "favorites"
        const val COLUMN_ID = "id"
        const val COLUMN_USER_UUID = "user_uuid"
        const val COLUMN_PRODUCT_UUID = "product_uuid"
        const val COLUMN_CREATED_AT = "created_at"

        val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_UUID TEXT NOT NULL,
                $COLUMN_PRODUCT_UUID TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_USER_UUID) REFERENCES ${Users.TABLE_NAME}(${Users.COLUMN_UUID}) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_PRODUCT_UUID) REFERENCES ${Products.TABLE_NAME}(${Products.COLUMN_UUID}) ON DELETE CASCADE,
                UNIQUE ($COLUMN_USER_UUID, $COLUMN_PRODUCT_UUID)
            )
        """.trimIndent()
    }
}
