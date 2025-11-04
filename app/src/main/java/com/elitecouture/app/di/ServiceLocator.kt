package com.elitecouture.app.di

import android.content.Context
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.dao.ProductDao
import com.elitecouture.app.data.local.dao.UserDao
import com.elitecouture.app.data.repository.AuthRepository
import com.elitecouture.app.data.repository.ProductRepository
import com.elitecouture.app.data.session.SessionManager

/**
 * Simple service locator to keep object creation in one place. Suitable for this academic project.
 */
object ServiceLocator {
    private fun provideDatabase(context: Context): EliteCoutureDatabase =
        EliteCoutureDatabase.getInstance(context.applicationContext)

    fun provideAuthRepository(context: Context): AuthRepository {
        val database = provideDatabase(context)
        return AuthRepository(UserDao(database))
    }

    fun provideProductRepository(context: Context): ProductRepository {
        val database = provideDatabase(context)
        return ProductRepository(ProductDao(database))
    }

    fun provideSessionManager(context: Context): SessionManager =
        SessionManager(context.applicationContext)
}
