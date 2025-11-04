package com.elitecouture.app.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.contract.DatabaseContract
import com.elitecouture.app.data.local.entity.ProductEntity

/** Access helpers for the products table. */
class ProductDao(private val database: EliteCoutureDatabase) {
    fun insertOrReplace(entity: ProductEntity): Long {
        val values = ContentValues().apply {
            if (entity.id != 0L) {
                put(DatabaseContract.Products.COLUMN_ID, entity.id)
            }
            put(DatabaseContract.Products.COLUMN_UUID, entity.uuid)
            put(DatabaseContract.Products.COLUMN_NAME, entity.name)
            put(DatabaseContract.Products.COLUMN_TYPE, entity.type)
            put(DatabaseContract.Products.COLUMN_GENDER, entity.gender)
            put(DatabaseContract.Products.COLUMN_DESCRIPTION, entity.description)
            put(DatabaseContract.Products.COLUMN_PRICE, entity.price)
            put(DatabaseContract.Products.COLUMN_STOCK, entity.stock)
            put(DatabaseContract.Products.COLUMN_IMAGE_URL, entity.imageUrl)
            put(DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST, if (entity.isVisibleToGuest) 1 else 0)
        }

        return database.writableDatabase.insertWithOnConflict(
            DatabaseContract.Products.TABLE_NAME,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun fetchAll(includeGuestHidden: Boolean): List<ProductEntity> {
        val columns = arrayOf(
            DatabaseContract.Products.COLUMN_ID,
            DatabaseContract.Products.COLUMN_UUID,
            DatabaseContract.Products.COLUMN_NAME,
            DatabaseContract.Products.COLUMN_TYPE,
            DatabaseContract.Products.COLUMN_GENDER,
            DatabaseContract.Products.COLUMN_DESCRIPTION,
            DatabaseContract.Products.COLUMN_PRICE,
            DatabaseContract.Products.COLUMN_STOCK,
            DatabaseContract.Products.COLUMN_IMAGE_URL,
            DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST
        )

        val selection: String?
        val selectionArgs: Array<String>?

        if (includeGuestHidden) {
            selection = null
            selectionArgs = null
        } else {
            selection = "${DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST} = ?"
            selectionArgs = arrayOf("1")
        }

        val cursor = database.readableDatabase.query(
            DatabaseContract.Products.TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            "${DatabaseContract.Products.COLUMN_NAME} COLLATE NOCASE ASC"
        )

        val items = mutableListOf<ProductEntity>()
        cursor.use {
            while (it.moveToNext()) {
                items.add(mapToEntity(it))
            }
        }
        return items
    }

    fun deleteAll() {
        database.writableDatabase.delete(DatabaseContract.Products.TABLE_NAME, null, null)
    }

    private fun mapToEntity(cursor: Cursor): ProductEntity {
        return ProductEntity(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_ID)),
            uuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_UUID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_DESCRIPTION)),
            type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_TYPE)),
            gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_GENDER)),
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_PRICE)),
            stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_STOCK)),
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_IMAGE_URL)),
            isVisibleToGuest = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Products.COLUMN_IS_VISIBLE_TO_GUEST)) == 1
        )
    }
}
