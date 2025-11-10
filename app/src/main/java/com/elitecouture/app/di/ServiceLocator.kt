package com.elitecouture.app.di

import android.content.Context
import com.elitecouture.app.data.repository.SupabaseCartRepository
import com.elitecouture.app.data.repository.SupabaseFavoriteRepository
import com.elitecouture.app.data.repository.SupabaseProductRepository
import com.elitecouture.app.data.repository.SupabaseUserRepository
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.usecase.auth.EnableGuestAccessUseCase
import com.elitecouture.app.domain.usecase.auth.LoginUserUseCase
import com.elitecouture.app.domain.usecase.auth.LogoutUserUseCase
import com.elitecouture.app.domain.usecase.auth.RegisterUserUseCase
import com.elitecouture.app.domain.usecase.cart.AddToCartUseCase
import com.elitecouture.app.domain.usecase.cart.ClearCartUseCase
import com.elitecouture.app.domain.usecase.cart.GetUserCartUseCase
import com.elitecouture.app.domain.usecase.cart.IsProductInCartUseCase
import com.elitecouture.app.domain.usecase.cart.RemoveFromCartUseCase
import com.elitecouture.app.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.elitecouture.app.domain.usecase.favorites.AddProductToFavoritesUseCase
import com.elitecouture.app.domain.usecase.favorites.GetUserFavoritesUseCase
import com.elitecouture.app.domain.usecase.favorites.IsProductFavoriteUseCase
import com.elitecouture.app.domain.usecase.favorites.RemoveProductFromFavoritesUseCase
import com.elitecouture.app.domain.usecase.product.GetProductCatalogUseCase
import com.elitecouture.app.domain.usecase.product.GetProductsByCategoryUseCase
import com.elitecouture.app.domain.usecase.product.GetProductsByGenderUseCase
import com.elitecouture.app.domain.usecase.product.SearchProductsUseCase
import com.elitecouture.app.domain.usecase.profile.UpdateProfileUseCase

/**
 * ServiceLocator - Contenedor de dependencias centralizado.
 * 
 * ✨ MIGRADO A SUPABASE ✨
 * 
 * Proporciona instancias únicas de:
 * - Repositorios de Supabase (SupabaseUserRepository, SupabaseProductRepository, etc.)
 * - SessionManager
 * - UseCases organizados por features
 * 
 * Todas las operaciones ahora se realizan en la nube usando Supabase.
 * SQLite local ha sido completamente eliminado.
 */
object ServiceLocator {
    
    // ============================================
    // SUPABASE REPOSITORIES - Singleton instances
    // ============================================
    
    private val supabaseUserRepository: SupabaseUserRepository by lazy {
        SupabaseUserRepository()
    }
    
    private val supabaseProductRepository: SupabaseProductRepository by lazy {
        SupabaseProductRepository()
    }
    
    private val supabaseFavoriteRepository: SupabaseFavoriteRepository by lazy {
        SupabaseFavoriteRepository()
    }
    
    private val supabaseCartRepository: SupabaseCartRepository by lazy {
        SupabaseCartRepository()
    }
    
    /**
     * Proporciona instancia de SupabaseUserRepository.
     */
    fun provideSupabaseUserRepository(): SupabaseUserRepository = supabaseUserRepository

    /**
     * Proporciona instancia de SupabaseProductRepository.
     */
    fun provideSupabaseProductRepository(): SupabaseProductRepository = supabaseProductRepository
    
    /**
     * Proporciona instancia de SupabaseFavoriteRepository.
     */
    fun provideSupabaseFavoriteRepository(): SupabaseFavoriteRepository = supabaseFavoriteRepository
    
    /**
     * Proporciona instancia de SupabaseCartRepository.
     */
    fun provideSupabaseCartRepository(): SupabaseCartRepository = supabaseCartRepository

    // ============================================
    // SESSION MANAGER
    // ============================================
    
    /**
     * Proporciona instancia de SessionManager.
     */
    fun provideSessionManager(context: Context): SessionManager =
        SessionManager(context.applicationContext)

    // ============================================
    // USE CASES - AUTH
    // ============================================
    
    /**
     * Proporciona LoginUserUseCase.
     */
    fun provideLoginUserUseCase(context: Context): LoginUserUseCase {
        return LoginUserUseCase(
            userRepository = supabaseUserRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona RegisterUserUseCase.
     */
    fun provideRegisterUserUseCase(context: Context): RegisterUserUseCase {
        return RegisterUserUseCase(
            userRepository = supabaseUserRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona EnableGuestAccessUseCase.
     */
    fun provideEnableGuestAccessUseCase(context: Context): EnableGuestAccessUseCase {
        return EnableGuestAccessUseCase(
            userRepository = supabaseUserRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona LogoutUserUseCase.
     */
    fun provideLogoutUserUseCase(context: Context): LogoutUserUseCase {
        return LogoutUserUseCase(
            sessionManager = provideSessionManager(context)
        )
    }

    // ============================================
    // USE CASES - PROFILE
    // ============================================
    
    /**
     * Proporciona UpdateProfileUseCase.
     */
    fun provideUpdateProfileUseCase(context: Context): UpdateProfileUseCase {
        return UpdateProfileUseCase(
            userRepository = supabaseUserRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    // ============================================
    // USE CASES - PRODUCT
    // ============================================
    
    /**
     * Proporciona GetProductCatalogUseCase.
     */
    fun provideGetProductCatalogUseCase(context: Context): GetProductCatalogUseCase {
        return GetProductCatalogUseCase(
            productRepository = supabaseProductRepository
        )
    }

    /**
     * Proporciona GetProductsByCategoryUseCase.
     */
    fun provideGetProductsByCategoryUseCase(context: Context): GetProductsByCategoryUseCase {
        return GetProductsByCategoryUseCase(
            productRepository = supabaseProductRepository
        )
    }

    /**
     * Proporciona GetProductsByGenderUseCase.
     */
    fun provideGetProductsByGenderUseCase(context: Context): GetProductsByGenderUseCase {
        return GetProductsByGenderUseCase(
            productRepository = supabaseProductRepository
        )
    }

    /**
     * Proporciona SearchProductsUseCase.
     */
    fun provideSearchProductsUseCase(context: Context): SearchProductsUseCase {
        return SearchProductsUseCase(
            productRepository = supabaseProductRepository
        )
    }

    // ============================================
    // USE CASES - FAVORITES
    // ============================================
    
    /**
     * Proporciona AddProductToFavoritesUseCase.
     */
    fun provideAddProductToFavoritesUseCase(context: Context): AddProductToFavoritesUseCase {
        return AddProductToFavoritesUseCase(
            favoriteRepository = supabaseFavoriteRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona RemoveProductFromFavoritesUseCase.
     */
    fun provideRemoveProductFromFavoritesUseCase(context: Context): RemoveProductFromFavoritesUseCase {
        return RemoveProductFromFavoritesUseCase(
            favoriteRepository = supabaseFavoriteRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona IsProductFavoriteUseCase.
     */
    fun provideIsProductFavoriteUseCase(context: Context): IsProductFavoriteUseCase {
        return IsProductFavoriteUseCase(
            favoriteRepository = supabaseFavoriteRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona GetUserFavoritesUseCase.
     */
    fun provideGetUserFavoritesUseCase(context: Context): GetUserFavoritesUseCase {
        return GetUserFavoritesUseCase(
            favoriteRepository = supabaseFavoriteRepository,
            productRepository = supabaseProductRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    // ============================================
    // USE CASES - CART
    // ============================================
    
    /**
     * Proporciona AddToCartUseCase.
     */
    fun provideAddToCartUseCase(context: Context): AddToCartUseCase {
        return AddToCartUseCase(
            cartRepository = supabaseCartRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona UpdateCartItemQuantityUseCase.
     */
    fun provideUpdateCartItemQuantityUseCase(context: Context): UpdateCartItemQuantityUseCase {
        return UpdateCartItemQuantityUseCase(
            cartRepository = supabaseCartRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona RemoveFromCartUseCase.
     */
    fun provideRemoveFromCartUseCase(context: Context): RemoveFromCartUseCase {
        return RemoveFromCartUseCase(
            cartRepository = supabaseCartRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona GetUserCartUseCase.
     */
    fun provideGetUserCartUseCase(context: Context): GetUserCartUseCase {
        return GetUserCartUseCase(
            cartRepository = supabaseCartRepository,
            productRepository = supabaseProductRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona ClearCartUseCase.
     */
    fun provideClearCartUseCase(context: Context): ClearCartUseCase {
        return ClearCartUseCase(
            cartRepository = supabaseCartRepository,
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona IsProductInCartUseCase.
     */
    fun provideIsProductInCartUseCase(context: Context): IsProductInCartUseCase {
        return IsProductInCartUseCase(
            cartRepository = supabaseCartRepository,
            sessionManager = provideSessionManager(context)
        )
    }
}
