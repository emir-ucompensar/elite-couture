package com.elitecouture.app.data.repository

import com.elitecouture.app.data.remote.SupabaseCartService
import com.elitecouture.app.domain.model.CartItem

/**
 * Cart repository using Supabase as backend.
 * 
 * This repository replaces the local SQLite implementation with cloud-based storage.
 * All operations are now asynchronous and use Kotlin coroutines.
 */
class SupabaseCartRepository {
    
    /**
     * Adds a product to user's cart or updates quantity if already exists.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @param quantity Product quantity
     * @return Created/updated cart item
     */
    suspend fun addToCart(userUuid: String, productUuid: String, quantity: Int = 1): CartItem {
        return SupabaseCartService.addToCart(userUuid, productUuid, quantity)
    }
    
    /**
     * Retrieves all cart items for a user.
     * 
     * @param userUuid User UUID
     * @return List of cart items
     */
    suspend fun getCartItems(userUuid: String): List<CartItem> {
        return SupabaseCartService.getCartItems(userUuid)
    }
    
    /**
     * Gets a specific cart item.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @return Cart item if found, null otherwise
     */
    suspend fun getCartItem(userUuid: String, productUuid: String): CartItem? {
        return SupabaseCartService.getCartItem(userUuid, productUuid)
    }
    
    /**
     * Gets total item count in cart.
     * 
     * @param userUuid User UUID
     * @return Total number of items (sum of quantities)
     */
    suspend fun getCartItemCount(userUuid: String): Int {
        return SupabaseCartService.getCartItemCount(userUuid)
    }
    
    /**
     * Updates quantity of a cart item.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     * @param newQuantity New quantity (use 0 or negative to remove)
     */
    suspend fun updateQuantity(userUuid: String, productUuid: String, newQuantity: Int) {
        SupabaseCartService.updateQuantity(userUuid, productUuid, newQuantity)
    }
    
    /**
     * Removes a product from user's cart.
     * 
     * @param userUuid User UUID
     * @param productUuid Product UUID
     */
    suspend fun removeFromCart(userUuid: String, productUuid: String) {
        SupabaseCartService.removeFromCart(userUuid, productUuid)
    }
    
    /**
     * Clears all items from user's cart.
     * 
     * @param userUuid User UUID
     */
    suspend fun clearCart(userUuid: String) {
        SupabaseCartService.clearCart(userUuid)
    }
}
