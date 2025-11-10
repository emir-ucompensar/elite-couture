package com.elitecouture.app.data.remote

import com.elitecouture.app.domain.model.CartItem
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * Supabase service for cart operations (CRUD).
 * 
 * This service handles all cart-related operations with Supabase PostgreSQL,
 * replacing the local SQLite implementation.
 */
object SupabaseCartService {
    
    /**
     * Data class representing the cart_items table schema in Supabase.
     */
    @Serializable
    data class CartItemDto(
        val id: Long? = null,
        val user_uuid: String,
        val product_uuid: String,
        val quantity: Int = 1,
        val added_at: Long = System.currentTimeMillis()
    )
    
    /**
     * Converts domain model to DTO for Supabase operations.
     */
    private fun CartItem.toDto(): CartItemDto = CartItemDto(
        id = if (id == 0L) null else id,
        user_uuid = userUuid,
        product_uuid = productUuid,
        quantity = quantity,
        added_at = addedAt
    )
    
    /**
     * Converts DTO to domain model.
     */
    private fun CartItemDto.toDomain(): CartItem = CartItem(
        id = id ?: 0L,
        userUuid = user_uuid,
        productUuid = product_uuid,
        quantity = quantity,
        addedAt = added_at
    )
    
    // ======================================================================
    // CREATE
    // ======================================================================
    
    /**
     * Adds a product to user's cart or updates quantity if already exists.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @param quantity Product quantity
     * @return The created/updated cart item
     * @throws Exception if operation fails
     */
    suspend fun addToCart(userUuid: String, productUuid: String, quantity: Int = 1): CartItem = withContext(Dispatchers.IO) {
        // Check if item already exists
        val existing = try {
            SupabaseClientProvider.client
                .from("cart_items")
                .select {
                    filter {
                        eq("user_uuid", userUuid)
                        eq("product_uuid", productUuid)
                    }
                }
                .decodeSingle<CartItemDto>()
        } catch (e: Exception) {
            null
        }
        
        if (existing != null) {
            // Update quantity
            val newQuantity = existing.quantity + quantity
            updateQuantity(userUuid, productUuid, newQuantity)
            existing.copy(quantity = newQuantity).toDomain()
        } else {
            // Insert new item
            val dto = CartItemDto(
                user_uuid = userUuid,
                product_uuid = productUuid,
                quantity = quantity
            )
            
            val result = SupabaseClientProvider.client
                .from("cart_items")
                .insert(dto) {
                    select()
                }
                .decodeSingle<CartItemDto>()
            
            result.toDomain()
        }
    }
    
    // ======================================================================
    // READ
    // ======================================================================
    
    /**
     * Retrieves all cart items for a user.
     * 
     * @param userUuid User UUID
     * @return List of cart items
     */
    suspend fun getCartItems(userUuid: String): List<CartItem> = withContext(Dispatchers.IO) {
        val results = SupabaseClientProvider.client
            .from("cart_items")
            .select {
                filter {
                    eq("user_uuid", userUuid)
                }
            }
            .decodeList<CartItemDto>()
        
        results.map { it.toDomain() }
    }
    
    /**
     * Gets a specific cart item.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return Cart item if found, null otherwise
     */
    suspend fun getCartItem(userUuid: String, productUuid: String): CartItem? = withContext(Dispatchers.IO) {
        try {
            val result = SupabaseClientProvider.client
                .from("cart_items")
                .select {
                    filter {
                        eq("user_uuid", userUuid)
                        eq("product_uuid", productUuid)
                    }
                }
                .decodeSingle<CartItemDto>()
            
            result.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Gets total item count in cart.
     * 
     * @param userUuid User UUID
     * @return Total number of items (sum of quantities)
     */
    suspend fun getCartItemCount(userUuid: String): Int = withContext(Dispatchers.IO) {
        val items = getCartItems(userUuid)
        items.sumOf { it.quantity }
    }
    
    // ======================================================================
    // UPDATE
    // ======================================================================
    
    /**
     * Updates quantity of a cart item.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @param newQuantity New quantity (use 0 or negative to remove)
     * @throws Exception if update fails
     */
    suspend fun updateQuantity(userUuid: String, productUuid: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            // Remove item if quantity is 0 or negative
            removeFromCart(userUuid, productUuid)
        } else {
            SupabaseClientProvider.client
                .from("cart_items")
                .update(
                    mapOf("quantity" to newQuantity)
                ) {
                    filter {
                        eq("user_uuid", userUuid)
                        eq("product_uuid", productUuid)
                    }
                }
        }
    }
    
    // ======================================================================
    // DELETE
    // ======================================================================
    
    /**
     * Removes a product from user's cart.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @throws Exception if deletion fails
     */
    suspend fun removeFromCart(userUuid: String, productUuid: String) = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("cart_items")
            .delete {
                filter {
                    eq("user_uuid", userUuid)
                    eq("product_uuid", productUuid)
                }
            }
    }
    
    /**
     * Clears all items from user's cart.
     * 
     * @param userUuid User UUID
     * @throws Exception if deletion fails
     */
    suspend fun clearCart(userUuid: String) = withContext(Dispatchers.IO) {
        SupabaseClientProvider.client
            .from("cart_items")
            .delete {
                filter {
                    eq("user_uuid", userUuid)
                }
            }
    }
}
