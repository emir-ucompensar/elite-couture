package com.elitecouture.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.elitecouture.app.data.local.contract.DatabaseContract

/**
 * Thin wrapper around [SQLiteOpenHelper] that owns the schema creation for the app.
 * The DAO layer should request readable/writable instances through this helper only.
 */
class EliteCoutureDatabase private constructor(
    context: Context
) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DatabaseContract.Users.CREATE_TABLE)
        db.execSQL(DatabaseContract.Products.CREATE_TABLE)
        db.execSQL(DatabaseContract.CartItems.CREATE_TABLE)
        db.execSQL(DatabaseContract.Favorites.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Academic project: easiest path is to recreate the schema on each upgrade.
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Favorites.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.CartItems.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Products.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Users.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        @Volatile
        private var instance: EliteCoutureDatabase? = null

        fun getInstance(context: Context): EliteCoutureDatabase {
            return instance ?: synchronized(this) {
                instance ?: EliteCoutureDatabase(context.applicationContext).also { instance = it }
            }
        }
    }
}
