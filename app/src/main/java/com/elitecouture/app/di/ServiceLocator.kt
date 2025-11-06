package com.elitecouture.app.di

import android.content.Context
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.dao.FavoriteDao
import com.elitecouture.app.data.local.dao.ProductDao
import com.elitecouture.app.data.local.dao.UserDao
import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.data.seed.DatabaseSeeder
import com.elitecouture.app.data.session.SessionManager
import com.elitecouture.app.domain.usecase.auth.EnableGuestAccessUseCase
import com.elitecouture.app.domain.usecase.auth.LoginUserUseCase
import com.elitecouture.app.domain.usecase.auth.LogoutUserUseCase
import com.elitecouture.app.domain.usecase.auth.RegisterUserUseCase
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
 * Proporciona instancias únicas de:
 * - Repositorios (AuthRepository, ProductRepository)
 * - SessionManager
 * - UseCases organizados por features
 * 
 * Mantiene todas las dependencias en un solo lugar,
 * facilitando testing y mantenimiento.
 */
object ServiceLocator {
    
    // ============================================
    // DATABASE & DAOs
    // ============================================
    
    /**
     * Proporciona instancia de la base de datos.
     */
    private fun provideDatabase(context: Context): EliteCoutureDatabase =
        EliteCoutureDatabase.getInstance(context.applicationContext)

    // ============================================
    // REPOSITORIES
    // ============================================
    
    /**
     * Proporciona instancia de AuthRepository.
     */
    fun provideAuthRepository(context: Context): AuthRepository {
        val database = provideDatabase(context)
        return AuthRepository(UserDao(database))
    }

    /**
     * Proporciona instancia de ProductRepository.
     */
    fun provideProductRepository(context: Context): ProductRepository {
        val database = provideDatabase(context)
        return ProductRepository(ProductDao(database))
    }

    // ============================================
    // SESSION MANAGER
    // ============================================
    
    /**
     * Proporciona instancia de SessionManager.
     */
    fun provideSessionManager(context: Context): SessionManager =
        SessionManager(context.applicationContext)

    // ============================================
    // DATABASE SEEDER
    // ============================================
    
    /**
     * Proporciona instancia de DatabaseSeeder.
     */
    fun provideDatabaseSeeder(context: Context): DatabaseSeeder {
        val database = provideDatabase(context)
        return DatabaseSeeder(
            context = context.applicationContext,
            productDao = ProductDao(database)
        )
    }

    // ============================================
    // USE CASES - AUTH
    // ============================================
    
    /**
     * Proporciona LoginUserUseCase.
     */
    fun provideLoginUserUseCase(context: Context): LoginUserUseCase {
        return LoginUserUseCase(
            authRepository = provideAuthRepository(context),
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona RegisterUserUseCase.
     */
    fun provideRegisterUserUseCase(context: Context): RegisterUserUseCase {
        return RegisterUserUseCase(
            authRepository = provideAuthRepository(context),
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona EnableGuestAccessUseCase.
     */
    fun provideEnableGuestAccessUseCase(context: Context): EnableGuestAccessUseCase {
        return EnableGuestAccessUseCase(
            authRepository = provideAuthRepository(context),
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
            authRepository = provideAuthRepository(context),
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
            productRepository = provideProductRepository(context)
        )
    }

    /**
     * Proporciona GetProductsByCategoryUseCase.
     */
    fun provideGetProductsByCategoryUseCase(context: Context): GetProductsByCategoryUseCase {
        return GetProductsByCategoryUseCase(
            productRepository = provideProductRepository(context)
        )
    }

    /**
     * Proporciona GetProductsByGenderUseCase.
     */
    fun provideGetProductsByGenderUseCase(context: Context): GetProductsByGenderUseCase {
        return GetProductsByGenderUseCase(
            productRepository = provideProductRepository(context)
        )
    }

    /**
     * Proporciona SearchProductsUseCase.
     */
    fun provideSearchProductsUseCase(context: Context): SearchProductsUseCase {
        return SearchProductsUseCase(
            productRepository = provideProductRepository(context)
        )
    }

    // ============================================
    // USE CASES - FAVORITES
    // ============================================
    
    /**
     * Proporciona AddProductToFavoritesUseCase.
     */
    fun provideAddProductToFavoritesUseCase(context: Context): AddProductToFavoritesUseCase {
        val database = provideDatabase(context)
        return AddProductToFavoritesUseCase(
            favoriteDao = FavoriteDao(database),
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona RemoveProductFromFavoritesUseCase.
     */
    fun provideRemoveProductFromFavoritesUseCase(context: Context): RemoveProductFromFavoritesUseCase {
        val database = provideDatabase(context)
        return RemoveProductFromFavoritesUseCase(
            favoriteDao = FavoriteDao(database),
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona IsProductFavoriteUseCase.
     */
    fun provideIsProductFavoriteUseCase(context: Context): IsProductFavoriteUseCase {
        val database = provideDatabase(context)
        return IsProductFavoriteUseCase(
            favoriteDao = FavoriteDao(database),
            sessionManager = provideSessionManager(context)
        )
    }

    /**
     * Proporciona GetUserFavoritesUseCase.
     */
    fun provideGetUserFavoritesUseCase(context: Context): GetUserFavoritesUseCase {
        val database = provideDatabase(context)
        return GetUserFavoritesUseCase(
            favoriteDao = FavoriteDao(database),
            sessionManager = provideSessionManager(context)
        )
    }
}
