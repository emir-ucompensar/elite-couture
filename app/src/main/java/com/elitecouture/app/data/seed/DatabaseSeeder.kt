package com.elitecouture.app.data.seed

import android.content.Context
import android.util.Log
import com.elitecouture.app.data.local.dao.ProductDao
import com.elitecouture.app.domain.model.Product
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * DatabaseSeeder handles initial population of the database with demo data.
 * Reads product data from JSON assets and inserts them into the database.
 */
class DatabaseSeeder(
    private val context: Context,
    private val productDao: ProductDao
) {
    companion object {
        private const val TAG = "DatabaseSeeder"
        private const val PRODUCTS_JSON_FILE = "products_seed.json"
        private const val SHARED_PREFS_NAME = "EliteCouturePrefs"
        private const val KEY_DB_SEEDED = "database_seeded"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Seeds the database if it hasn't been seeded before.
     * Uses SharedPreferences to track seeding status.
     */
    suspend fun seedDatabaseIfNeeded() {
        val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val alreadySeeded = prefs.getBoolean(KEY_DB_SEEDED, false)

        if (!alreadySeeded) {
            Log.d(TAG, "Database not seeded yet. Starting seeding process...")
            seedProducts()
            
            // Mark as seeded
            prefs.edit().putBoolean(KEY_DB_SEEDED, true).apply()
            Log.d(TAG, "Database seeding completed successfully")
        } else {
            Log.d(TAG, "Database already seeded, skipping...")
        }
    }

    /**
     * Force re-seed the database (useful for development/testing)
     */
    suspend fun forceSeedDatabase() {
        Log.d(TAG, "Force seeding database...")
        productDao.deleteAll()
        seedProducts()
        
        val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DB_SEEDED, true).apply()
        Log.d(TAG, "Force seeding completed")
    }

    /**
     * Loads products from JSON and inserts them into database
     */
    private suspend fun seedProducts() {
        try {
            val jsonString = loadJsonFromAssets(PRODUCTS_JSON_FILE)
            val seedData = json.decodeFromString<ProductSeedData>(jsonString)
            
            Log.d(TAG, "Loaded ${seedData.products.size} products from JSON")
            
            seedData.products.forEach { dto ->
                // Convertir array de nombres de imágenes a paths completos de assets
                val imagePaths = if (dto.images.isNotEmpty()) {
                    // Mapear nombres a extensión WebP (convertidas por script Python)
                    dto.images.map { imageName ->
                        // Si el nombre ya tiene extensión, cambiarla a .webp, si no agregarla
                        val webpName = if (imageName.contains(".")) {
                            imageName.replace(Regex("\\.[^.]+$"), ".webp")
                        } else {
                            "$imageName.webp"
                        }
                        "file:///android_asset/images/$webpName"
                    }
                } else if (dto.imageResource != null) {
                    // Legacy: intentar cargar desde drawable (fallback)
                    getDrawableResourceId(dto.imageResource)?.let {
                        listOf("drawable://$it")
                    } ?: emptyList()
                } else {
                    emptyList()
                }
                
                val product = Product(
                    uuid = dto.uuid,
                    name = dto.name,
                    description = dto.description,
                    type = dto.type,
                    gender = dto.gender,
                    price = dto.price,
                    stock = dto.stock,
                    images = imagePaths,
                    tags = dto.tags,
                    isVisibleToGuest = dto.isVisibleToGuest
                )
                
                productDao.insert(product)
                Log.d(TAG, "Inserted product: ${product.name} with ${imagePaths.size} images")
            }
            
            Log.d(TAG, "Successfully seeded ${seedData.products.size} products")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding products", e)
            throw e
        }
    }

    /**
     * Loads JSON file content from assets folder
     */
    private fun loadJsonFromAssets(fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading JSON from assets: $fileName", e)
            throw e
        }
    }

    /**
     * Gets drawable resource ID from resource name
     */
    private fun getDrawableResourceId(resourceName: String): Int? {
        val resourceId = context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName
        )
        
        return if (resourceId != 0) {
            resourceId
        } else {
            Log.w(TAG, "Drawable resource not found: $resourceName")
            null
        }
    }
}
