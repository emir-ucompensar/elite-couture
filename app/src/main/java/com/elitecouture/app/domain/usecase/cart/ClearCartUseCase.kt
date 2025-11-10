package com.elitecouture.app.domain.usecase.cart

import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case for clearing all items from the shopping cart.
 * 
 * @param cartRepository Supabase repository for cart operations
 * @param sessionManager Session manager to get current user UUID
 */
class ClearCartUseCase(
    private val cartRepository: SupabaseCartRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Clears all items from the user's cart.
     * Returns the number of rows deleted, or 0 if user is not logged in.
     * 
     * @return Number of rows deleted
     */
    suspend operator fun invoke(): Int = withContext(Dispatchers.IO) {
        val userUuid = sessionManager.getUserUuid() ?: return@withContext 0
        try {
            cartRepository.clearCart(userUuid)
            1 // Supabase doesn't return affected rows, assume success
        } catch (e: Exception) {
            0
        }
    }
}
