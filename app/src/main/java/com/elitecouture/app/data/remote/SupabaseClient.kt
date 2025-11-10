package com.elitecouture.app.data.remote

import com.elitecouture.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.SupabaseClient

/**
 * Objeto singleton que proporciona el cliente de Supabase
 * para acceso a la base de datos y storage
 */
object SupabaseClientProvider {
    
    /**
     * Cliente de Supabase configurado con Postgrest y Storage
     */
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
}
