package com.elitecouture.app.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.contract.DatabaseContract
import com.elitecouture.app.data.local.entity.FavoriteEntity
import com.elitecouture.app.data.local.entity.ProductEntity
import com.elitecouture.app.domain.model.Favorite
import com.elitecouture.app.domain.model.FavoriteWithProduct

/**
 * Data Access Object for favorites table.
 * Provides CRUD operations for managing user favorites.
 */
class FavoriteDao(private val database: EliteCoutureDatabase) {
    
    companion object {
        private const val TAG = "FavoriteDao"
    }

    /**
     * Adds a product to user's favorites.
     * Returns the ID of the inserted row, or -1 if an error occurred.
     */
    fun addFavorite(userUuid: String, productUuid: String): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.Favorites.COLUMN_USER_UUID, userUuid)
            put(DatabaseContract.Favorites.COLUMN_PRODUCT_UUID, productUuid)
            put(DatabaseContract.Favorites.COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        val result = database.writableDatabase.insertWithOnConflict(
            DatabaseContract.Favorites.TABLE_NAME,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE // Ignore if already exists
        )
        
        android.util.Log.d(TAG, "addFavorite() -> userUuid=$userUuid, productUuid=$productUuid, result=$result")
        return result
    }

    /**
     * Removes a product from user's favorites.
     * Returns the number of rows deleted.
     */
    fun removeFavorite(userUuid: String, productUuid: String): Int {
        val rowsDeleted = database.writableDatabase.delete(
            DatabaseContract.Favorites.TABLE_NAME,
            "${DatabaseContract.Favorites.COLUMN_USER_UUID} = ? AND ${DatabaseContract.Favorites.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid)
        )
        
        android.util.Log.d(TAG, "removeFavorite() -> userUuid=$userUuid, productUuid=$productUuid, rowsDeleted=$rowsDeleted")
        return rowsDeleted
    }

    /**
     * Checks if a product is in user's favorites.
     */
    fun isFavorite(userUuid: String, productUuid: String): Boolean {
        val cursor = database.readableDatabase.query(
            DatabaseContract.Favorites.TABLE_NAME,
            arrayOf(DatabaseContract.Favorites.COLUMN_ID),
            "${DatabaseContract.Favorites.COLUMN_USER_UUID} = ? AND ${DatabaseContract.Favorites.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid),
            null,
            null,
            null
        )

        val exists = cursor.use { it.count > 0 }
        android.util.Log.d(TAG, "isFavorite() -> userUuid=$userUuid, productUuid=$productUuid, exists=$exists")
        return exists
    }

    /**
     * Gets all favorites for a specific user.
     */
    fun getFavoritesByUser(userUuid: String): List<Favorite> {
        val favorites = mutableListOf<Favorite>()
        
        val cursor = database.readableDatabase.query(
            DatabaseContract.Favorites.TABLE_NAME,
            null,
            "${DatabaseContract.Favorites.COLUMN_USER_UUID} = ?",
            arrayOf(userUuid),
            null,
            null,
            "${DatabaseContract.Favorites.COLUMN_CREATED_AT} DESC" // Most recent first
        )

        cursor.use {
            while (it.moveToNext()) {
                favorites.add(cursorToFavoriteEntity(it).toDomain())
            }
        }

        return favorites
    }

    /**
     * Gets all favorites with complete product information for a user.
     * Performs a JOIN between favorites and products tables.
     */
    fun getFavoritesWithProducts(userUuid: String): List<FavoriteWithProduct> {
        android.util.Log.d(TAG, "getFavoritesWithProducts() -> userUuid=$userUuid")
        val favoritesWithProducts = mutableListOf<FavoriteWithProduct>()

        val query = """
            SELECT 
                f.${DatabaseContract.Favorites.COLUMN_ID} as fav_id,
                f.${DatabaseContract.Favorites.COLUMN_USER_UUID} as fav_user_uuid,
                f.${DatabaseContract.Favorites.COLUMN_PRODUCT_UUID} as fav_product_uuid,
                f.${DatabaseContract.Favorites.COLUMN_CREATED_AT} as fav_created_at,
                p.${DatabaseContract.Products.COLUMN_ID} as prod_id,
                p.${DatabaseContract.Products.COLUMN_UUID} as prod_uuid,
                p.${DatabaseContract.Products.COLUMN_NAME} as prod_name,
                p.${DatabaseContract.Products.COLUMN_DESCRIPTION} as prod_description,
                p.${DatabaseContract.Products.COLUMN_TYPE} as prod_type,
                p.${DatabaseContract.Products.COLUMN_GENDER} as prod_gender,
                p.${DatabaseContract.Products.COLUMN_PRICE} as prod_price,
                p.${DatabaseContract.Products.COLUMN_STOCK} as prod_stock,
                p.${DatabaseContract.Products.COLUMN_IMAGES} as prod_images,
                p.${DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST} as prod_is_visible
            FROM ${DatabaseContract.Favorites.TABLE_NAME} f
            INNER JOIN ${DatabaseContract.Products.TABLE_NAME} p 
                ON f.${DatabaseContract.Favorites.COLUMN_PRODUCT_UUID} = p.${DatabaseContract.Products.COLUMN_UUID}
            WHERE f.${DatabaseContract.Favorites.COLUMN_USER_UUID} = ?
            ORDER BY f.${DatabaseContract.Favorites.COLUMN_CREATED_AT} DESC
        """.trimIndent()

        val cursor = database.readableDatabase.rawQuery(query, arrayOf(userUuid))

        cursor.use {
            while (it.moveToNext()) {
                val favorite = FavoriteEntity(
                    id = it.getLong(it.getColumnIndexOrThrow("fav_id")),
                    userUuid = it.getString(it.getColumnIndexOrThrow("fav_user_uuid")),
                    productUuid = it.getString(it.getColumnIndexOrThrow("fav_product_uuid")),
                    createdAt = it.getLong(it.getColumnIndexOrThrow("fav_created_at"))
                )

                val product = ProductEntity(
                    id = it.getLong(it.getColumnIndexOrThrow("prod_id")),
                    uuid = it.getString(it.getColumnIndexOrThrow("prod_uuid")),
                    name = it.getString(it.getColumnIndexOrThrow("prod_name")),
                    description = it.getString(it.getColumnIndexOrThrow("prod_description")),
                    type = it.getString(it.getColumnIndexOrThrow("prod_type")),
                    gender = it.getString(it.getColumnIndexOrThrow("prod_gender")),
                    price = it.getDouble(it.getColumnIndexOrThrow("prod_price")),
                    stock = it.getInt(it.getColumnIndexOrThrow("prod_stock")),
                    images = it.getString(it.getColumnIndexOrThrow("prod_images")),
                    isVisibleToGuest = it.getInt(it.getColumnIndexOrThrow("prod_is_visible")) == 1
                )

                favoritesWithProducts.add(
                    FavoriteWithProduct(
                        favorite = favorite.toDomain(),
                        product = product.toDomain()
                    )
                )
            }
        }

        android.util.Log.d(TAG, "getFavoritesWithProducts() -> Found ${favoritesWithProducts.size} favorites")
        return favoritesWithProducts
    }

    /**
     * Converts a cursor row to a FavoriteEntity.
     */
    private fun cursorToFavoriteEntity(cursor: Cursor): FavoriteEntity {
        return FavoriteEntity(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Favorites.COLUMN_ID)),
            userUuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Favorites.COLUMN_USER_UUID)),
            productUuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Favorites.COLUMN_PRODUCT_UUID)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Favorites.COLUMN_CREATED_AT))
        )
    }
}
