package com.dicoding.andikas.githubuser.db

import android.net.Uri
import android.provider.BaseColumns

internal class DatabaseContract {
    companion object {
        const val AUTHORITY = "com.dicoding.andikas"
        const val SCHEME = "content"
    }

    internal class UserColumns: BaseColumns{
        companion object {
            const val TABLE_NAME = "favorite"
            const val AVATAR = "avatar"
            const val USERNAME = "username"
            const val NAME = "name"
            const val LOCATION = "location"
            const val COMPANY = "company"
            const val REPOSITORY = "repository"
            // const val FAVORITE = "favorite"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(TABLE_NAME)
                    .build()
        }
    }

}