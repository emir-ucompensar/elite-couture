package com.elitecouture.app.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.elitecouture.app.data.local.EliteCoutureDatabase
import com.elitecouture.app.data.local.contract.DatabaseContract
import com.elitecouture.app.data.local.entity.UserEntity

/**
 * Provides CRUD helpers for the users table. Operates synchronously by design for simplicity.
 * For production apps prefer Room or wrap calls inside a dispatcher to avoid blocking the main thread.
 */
class UserDao(private val database: EliteCoutureDatabase) {
    fun insert(entity: UserEntity): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.Users.COLUMN_UUID, entity.uuid)
            put(DatabaseContract.Users.COLUMN_EMAIL, entity.email)
            put(DatabaseContract.Users.COLUMN_PASSWORD, entity.password)
            put(DatabaseContract.Users.COLUMN_FIRST_NAME, entity.firstName)
            put(DatabaseContract.Users.COLUMN_LAST_NAME, entity.lastName)
            put(DatabaseContract.Users.COLUMN_ADDRESS, entity.address)
            put(DatabaseContract.Users.COLUMN_IS_GUEST, if (entity.isGuest) 1 else 0)
            put(DatabaseContract.Users.COLUMN_CREATED_AT, entity.createdAt)
        }

        return database.writableDatabase.insert(DatabaseContract.Users.TABLE_NAME, null, values)
    }
    
    fun update(entity: UserEntity): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Users.COLUMN_EMAIL, entity.email)
            put(DatabaseContract.Users.COLUMN_FIRST_NAME, entity.firstName)
            put(DatabaseContract.Users.COLUMN_LAST_NAME, entity.lastName)
            put(DatabaseContract.Users.COLUMN_ADDRESS, entity.address)
            // Password se actualiza por separado para mayor seguridad
        }

        return database.writableDatabase.update(
            DatabaseContract.Users.TABLE_NAME,
            values,
            "${DatabaseContract.Users.COLUMN_ID} = ?",
            arrayOf(entity.id.toString())
        )
    }
    
    fun updatePassword(userId: Long, newPassword: String): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Users.COLUMN_PASSWORD, newPassword)
        }

        return database.writableDatabase.update(
            DatabaseContract.Users.TABLE_NAME,
            values,
            "${DatabaseContract.Users.COLUMN_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun findByEmail(email: String): UserEntity? {
        val columns = arrayOf(
            DatabaseContract.Users.COLUMN_ID,
            DatabaseContract.Users.COLUMN_UUID,
            DatabaseContract.Users.COLUMN_EMAIL,
            DatabaseContract.Users.COLUMN_PASSWORD,
            DatabaseContract.Users.COLUMN_FIRST_NAME,
            DatabaseContract.Users.COLUMN_LAST_NAME,
            DatabaseContract.Users.COLUMN_ADDRESS,
            DatabaseContract.Users.COLUMN_IS_GUEST,
            DatabaseContract.Users.COLUMN_CREATED_AT
        )

        val cursor = database.readableDatabase.query(
            DatabaseContract.Users.TABLE_NAME,
            columns,
            "${DatabaseContract.Users.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        return cursor.use { mapToEntity(it) }
    }

    fun authenticate(email: String, password: String): UserEntity? {
        val columns = arrayOf(
            DatabaseContract.Users.COLUMN_ID,
            DatabaseContract.Users.COLUMN_UUID,
            DatabaseContract.Users.COLUMN_EMAIL,
            DatabaseContract.Users.COLUMN_PASSWORD,
            DatabaseContract.Users.COLUMN_FIRST_NAME,
            DatabaseContract.Users.COLUMN_LAST_NAME,
            DatabaseContract.Users.COLUMN_ADDRESS,
            DatabaseContract.Users.COLUMN_IS_GUEST,
            DatabaseContract.Users.COLUMN_CREATED_AT
        )

        val cursor = database.readableDatabase.query(
            DatabaseContract.Users.TABLE_NAME,
            columns,
            "${DatabaseContract.Users.COLUMN_EMAIL} = ? AND ${DatabaseContract.Users.COLUMN_PASSWORD} = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        return cursor.use { mapToEntity(it) }
    }

    fun deleteAll() {
        database.writableDatabase.delete(DatabaseContract.Users.TABLE_NAME, null, null)
    }

    private fun mapToEntity(cursor: Cursor): UserEntity? {
        if (!cursor.moveToFirst()) return null

        return UserEntity(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_ID)),
            uuid = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_UUID)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_EMAIL)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_PASSWORD)),
            firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_FIRST_NAME)),
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_LAST_NAME)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_ADDRESS)),
            isGuest = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_IS_GUEST)) == 1,
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_CREATED_AT))
        )
    }
}
