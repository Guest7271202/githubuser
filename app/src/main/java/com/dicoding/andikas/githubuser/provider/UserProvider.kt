package com.dicoding.andikas.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.andikas.githubuser.db.DatabaseContract.Companion.AUTHORITY
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.dicoding.andikas.githubuser.db.UserHelper

class UserProvider: ContentProvider() {
    companion object {
        private const val USER = 1
        private const val USER_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var userHelper: UserHelper

        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, USER)

            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", USER_ID)
        }
    }

    override fun onCreate(): Boolean {
        userHelper = UserHelper.getInstance(context as Context)
        userHelper.open()
        return true
    }

    override fun query(uri: Uri, p1: Array<String>?, p2: String?, p3: Array<String>?, p4: String?): Cursor? {
        return when (sUriMatcher.match(uri)) {
            USER -> userHelper.queryAll()
            USER_ID -> userHelper.queryByUsername(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (USER) {
            sUriMatcher.match(uri) -> userHelper.insert(contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, p2: String?, p3: Array<String>?): Int {
        val updated: Int = when (USER_ID) {
            sUriMatcher.match(uri) -> userHelper.update(uri.lastPathSegment.toString(), contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }

    override fun delete(uri: Uri, p1: String?, p2: Array<String>?): Int {
        val deleted: Int =  when (USER_ID) {
            sUriMatcher.match(uri) -> userHelper.deleteByUsername(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}