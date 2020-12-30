package com.dicoding.andikas.githubuser.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.USERNAME
import java.sql.SQLException
import kotlin.jvm.Throws

class UserHelper(context: Context) {

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE: UserHelper? = null

        fun getInstance(context: Context): UserHelper =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: UserHelper(context)
            }

        private lateinit var database: SQLiteDatabase
    }

    init {
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen){
            database.close()
        }
    }

    fun queryAll(): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                "$USERNAME ASC"
        )
    }

    fun queryByUsername(username: String): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$USERNAME  = ?",
                arrayOf(username),
                null,
                null,
                null
        )
    }

    fun insert(values: ContentValues?): Long{
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(username: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$USERNAME = ?", arrayOf(username))
    }

    fun deleteByUsername(username: String): Int {
        return database.delete(DATABASE_TABLE, "$USERNAME = '$username'", null)
    }

}