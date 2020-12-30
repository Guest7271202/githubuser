package com.dicoding.andikas.githubuser.db

import android.database.Cursor
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.AVATAR
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.COMPANY
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.LOCATION
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.NAME
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.dicoding.andikas.githubuser.model.User

object MappingHelper {

    fun mapCursorToArrayList(cursor: Cursor?): ArrayList<User>{
        val users = ArrayList<User>()

        cursor?.apply {
            while (moveToNext()){
                val avatar = getString(getColumnIndex(AVATAR))
                val username = getString(getColumnIndex(USERNAME))
                val name = getString(getColumnIndex(NAME))
                val location = getString(getColumnIndex(LOCATION))
                val company = getString(getColumnIndex(COMPANY))
                val repository = getString(getColumnIndex(REPOSITORY))

                users.add(User(
                        avatar, username, name, location, company, repository
                ))
            }
        }
        return users
    }

}