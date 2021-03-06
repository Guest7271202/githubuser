package com.dicoding.andikas.githubuser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
        var avatar: String?,
        var username: String?,
        var name: String?,
        var location: String?,
        var company: String?,
        var repository: String?
) : Parcelable