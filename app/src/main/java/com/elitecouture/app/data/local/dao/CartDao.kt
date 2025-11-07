package com.elitecouture.app.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.contract.DatabaseContract
import com.elitecouture.app.data.local.entity.CartItemEntity
import com.elitecouture.app.data.local.entity.ProductEntity
import com.elitecouture.app.domain.model.CartItem
import com.elitecouture.app.domain.model.CartItemWithProduct

/**
 * Data Access Object for cart_items table.
 * Provides CRUD operations for managing user shopping cart.
 */
class CartDao(private val database: EliteCoutureDatabase) {
    
    companion object {
        private const val TAG = "CartDao"
    }

    /**
     * Adds a product to user's cart with specified quantity.
     * If the product already exists, updates the quantity.
     * Returns the ID of the inserted/updated row, or -1 if an error occurred.
     */
    fun addToCart(userUuid: String, productUuid: String, quantity: Int = 1): Long {
        // Check if item already exists in cart
        val existingItem = getCartItem(userUuid, productUuid)
        
        if (existingItem != null) {
            // Update existing item quantity
            val newQuantity = existingItem.quantity + quantity
            updateQuantity(userUuid, productUuid, newQuantity)
            return existingItem.id
        }
        
        // Insert new item
        val values = ContentValues().apply {
            put(DatabaseContract.CartItems.COLUMN_USER_UUID, userUuid)
            put(DatabaseContract.CartItems.COLUMN_PRODUCT_UUID, productUuid)
            put(DatabaseContract.CartItems.COLUMN_QUANTITY, quantity)
            put(DatabaseContract.CartItems.COLUMN_ADDED_AT, System.currentTimeMillis())
        }

        val result = database.writableDatabase.insertWithOnConflict(
            DatabaseContract.CartItems.TABLE_NAME,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        
        android.util.Log.d(TAG, "addToCart() -> userUuid=$userUuid, productUuid=$productUuid, quantity=$quantity, result=$result")
        return result
    }

    /**
     * Updates the quantity of a cart item.
     * Returns the number of rows updated.
     */
    fun updateQuantity(userUuid: String, productUuid: String, newQuantity: Int): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.CartItems.COLUMN_QUANTITY, newQuantity)
        }

        val rowsUpdated = database.writableDatabase.update(
            DatabaseContract.CartItems.TABLE_NAME,
            values,
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ? AND ${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid)
        )
        
        android.util.Log.d(TAG, "updateQuantity() -> userUuid=$userUuid, productUuid=$productUuid, newQuantity=$newQuantity, rowsUpdated=$rowsUpdated")
        return rowsUpdated
    }

    /**
     * Removes a product from user's cart.
     * Returns the number of rows deleted.
     */
    fun removeFromCart(userUuid: String, productUuid: String): Int {
        val rowsDeleted = database.writableDatabase.delete(
            DatabaseContract.CartItems.TABLE_NAME,
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ? AND ${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid)
        )
        
        android.util.Log.d(TAG, "removeFromCart() -> userUuid=$userUuid, productUuid=$productUuid, rowsDeleted=$rowsDeleted")
        return rowsDeleted
    }

    /**
     * Checks if a product is in the user's cart.
     * Returns true if the product exists in cart, false otherwise.
     */
    fun isProductInCart(userUuid: String, productUuid: String): Boolean {
        val cursor = database.readableDatabase.query(
            DatabaseContract.CartItems.TABLE_NAME,
            arrayOf(DatabaseContract.CartItems.COLUMN_ID),
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ? AND ${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid),
            null,
            null,
            null
        )

        cursor.use {
            val exists = it.count > 0
            android.util.Log.d(TAG, "isProductInCart() -> userUuid=$userUuid, productUuid=$productUuid, exists=$exists")
            return exists
        }
    }

    /**
     * Gets a specific cart item for a user and product.
     */
    private fun getCartItem(userUuid: String, productUuid: String): CartItem? {
        val cursor = database.readableDatabase.query(
            DatabaseContract.CartItems.TABLE_NAME,
            null,
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ? AND ${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} = ?",
            arrayOf(userUuid, productUuid),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToCartItemEntity(it).toDomain()
            }
        }
        
        return null
    }

    /**
     * Gets all cart items for a specific user.
     */
    fun getCartItemsByUser(userUuid: String): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        
        val cursor = database.readableDatabase.query(
            DatabaseContract.CartItems.TABLE_NAME,
            null,
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ?",
            arrayOf(userUuid),
            null,
            null,
            "${DatabaseContract.CartItems.COLUMN_ADDED_AT} ASC" // Oldest first
        )

        cursor.use {
            while (it.moveToNext()) {
                cartItems.add(cursorToCartItemEntity(it).toDomain())
            }
        }

        return cartItems
    }

    /**
     * Gets all cart items with complete product information for a user.
     * Performs a JOIN between cart_items and products tables.
     */
    fun getCartItemsWithProducts(userUuid: String): List<CartItemWithProduct> {
        android.util.Log.d(TAG, "getCartItemsWithProducts() -> userUuid=$userUuid")
        val cartItemsWithProducts = mutableListOf<CartItemWithProduct>()

        val query = """
            SELECT 
                c.${DatabaseContract.CartItems.COLUMN_ID} as cart_id,
                c.${DatabaseContract.CartItems.COLUMN_USER_UUID} as cart_user_uuid,
                c.${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} as cart_product_uuid,
                c.${DatabaseContract.CartItems.COLUMN_QUANTITY} as cart_quantity,
                c.${DatabaseContract.CartItems.COLUMN_ADDED_AT} as cart_added_at,
                p.${DatabaseContract.Products.COLUMN_ID} as prod_id,
                p.${DatabaseContract.Products.COLUMN_UUID} as prod_uuid,
                p.${DatabaseContract.Products.COLUMN_NAME} as prod_name,
                p.${DatabaseContract.Products.COLUMN_DESCRIPTION} as prod_description,
                p.${DatabaseContract.Products.COLUMN_TYPE} as prod_type,
                p.${DatabaseContract.Products.COLUMN_GENDER} as prod_gender,
                p.${DatabaseContract.Products.COLUMN_PRICE} as prod_price,
                p.${DatabaseContract.Products.COLUMN_STOCK} as prod_stock,
                p.${DatabaseContract.Products.COLUMN_IMAGES} as prod_images,
                p.${DatabaseContract.Products.COLUMN_TAGS} as prod_tags,
                p.${DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST} as prod_is_visible
            FROM ${DatabaseContract.CartItems.TABLE_NAME} c
            INNER JOIN ${DatabaseContract.Products.TABLE_NAME} p 
                ON c.${DatabaseContract.CartItems.COLUMN_PRODUCT_UUID} = p.${DatabaseContract.Products.COLUMN_UUID}
            WHERE c.${DatabaseContract.CartItems.COLUMN_USER_UUID} = ?
            ORDER BY c.${DatabaseContract.CartItems.COLUMN_ADDED_AT} ASC
        """.trimIndent()

        val cursor = database.readableDatabase.rawQuery(query, arrayOf(userUuid))

        cursor.use {
            while (it.moveToNext()) {
                val cartItem = CartItemEntity(
                    id = it.getLong(it.getColumnIndexOrThrow("cart_id")),
                    userUuid = it.getString(it.getColumnIndexOrThrow("cart_user_uuid")),
                    productUuid = it.getString(it.getColumnIndexOrThrow("cart_product_uuid")),
                    quantity = it.getInt(it.getColumnIndexOrThrow("cart_quantity")),
                    addedAt = it.getLong(it.getColumnIndexOrThrow("cart_added_at"))
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
                    tags = it.getString(it.getColumnIndexOrThrow("prod_tags")),
                    isVisibleToGuest = it.getInt(it.getColumnIndexOrThrow("prod_is_visible")) == 1
                )

                cartItemsWithProducts.add(
                    CartItemWithProduct(
                        cartItem = cartItem.toDomain(),
                        product = product.toDomain()
                    )
                )
            }
        }

        android.util.Log.d(TAG, "getCartItemsWithProducts() -> Found ${cartItemsWithProducts.size} cart items")
        return cartItemsWithProducts
    }

    /**
     * Clears all cart items for a specific user.
     */
    fun clearCart(userUuid: String): Int {
        val rowsDeleted = database.writableDatabase.delete(
            DatabaseContract.CartItems.TABLE_NAME,
            "${DatabaseContract.CartItems.COLUMN_USER_UUID} = ?",
            arrayOf(userUuid)
        )
        
        android.util.Log.d(TAG, "clearCart() -> userUuid=$userUuid, rowsDeleted=$rowsDeleted")
        return rowsDeleted
    }

    /**
     * Converts a cursor row to a CartItemEntity.
     */
    private fun cursorToCartItemEntity(cursor: Cursor): CartItemEntity {
        return CartItemEntity(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.CartItems.COLUMN_ID)),
            userUuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.CartItems.COLUMN_USER_UUID)),
            productUuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.CartItems.COLUMN_PRODUCT_UUID)),
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartItems.COLUMN_QUANTITY)),
            addedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.CartItems.COLUMN_ADDED_AT))
        )
    }
}
