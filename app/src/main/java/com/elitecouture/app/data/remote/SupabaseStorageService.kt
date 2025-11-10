package com.elitecouture.app.data.remote

import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Servicio para gestionar el almacenamiento de imágenes en Supabase Storage
 */
object SupabaseStorageService {
    
    private const val TAG = "SupabaseStorage"
    private const val BUCKET_NAME = "product-images" // Nombre del bucket en Supabase
    
    /**
     * Sube una imagen al storage de Supabase
     * 
     * @param imageFile Archivo de imagen a subir
     * @param folderPath Ruta de carpeta opcional (ej: "products/", "users/")
     * @return URL pública de la imagen subida, o null si falla
     */
    suspend fun uploadImage(
        imageFile: File,
        folderPath: String = "products/"
    ): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val fullPath = "$folderPath$fileName"
            
            Log.d(TAG, "Subiendo imagen: $fullPath")
            
            // Subir archivo
            SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .upload(fullPath, imageFile.readBytes())
            
            // Obtener URL pública
            val publicUrl = SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .publicUrl(fullPath)
            
            Log.d(TAG, "Imagen subida exitosamente: $publicUrl")
            publicUrl
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir imagen", e)
            null
        }
    }
    
    /**
     * Sube una imagen desde URI (desde galería o cámara)
     * 
     * @param imageUri URI de la imagen
     * @param context Context para acceder al ContentResolver
     * @param folderPath Ruta de carpeta opcional
     * @return URL pública de la imagen subida, o null si falla
     */
    suspend fun uploadImageFromUri(
        imageUri: Uri,
        contentResolver: android.content.ContentResolver,
        folderPath: String = "products/"
    ): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val fullPath = "$folderPath$fileName"
            
            Log.d(TAG, "Subiendo imagen desde URI: $fullPath")
            
            // Leer bytes desde URI
            val inputStream = contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            
            if (bytes == null) {
                Log.e(TAG, "No se pudo leer la imagen desde URI")
                return@withContext null
            }
            
            // Subir archivo
            SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .upload(fullPath, bytes)
            
            // Obtener URL pública
            val publicUrl = SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .publicUrl(fullPath)
            
            Log.d(TAG, "Imagen subida exitosamente: $publicUrl")
            publicUrl
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir imagen desde URI", e)
            null
        }
    }
    
    /**
     * Elimina una imagen del storage
     * 
     * @param imageUrl URL completa de la imagen
     * @return true si se eliminó exitosamente, false si falló
     */
    suspend fun deleteImage(imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Extraer el path de la URL
            val path = imageUrl.substringAfter("$BUCKET_NAME/")
            
            Log.d(TAG, "Eliminando imagen: $path")
            
            SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .delete(path)
            
            Log.d(TAG, "Imagen eliminada exitosamente")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar imagen", e)
            false
        }
    }
    
    /**
     * Lista todas las imágenes en una carpeta
     * 
     * @param folderPath Ruta de la carpeta
     * @return Lista de URLs públicas de las imágenes
     */
    suspend fun listImages(folderPath: String = "products/"): List<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Listando imágenes en: $folderPath")
            
            val files = SupabaseClientProvider.client.storage
                .from(BUCKET_NAME)
                .list(folderPath)
            
            files.map { fileObject ->
                SupabaseClientProvider.client.storage
                    .from(BUCKET_NAME)
                    .publicUrl("$folderPath${fileObject.name}")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al listar imágenes", e)
            emptyList()
        }
    }
}
