package com.happ.coursegraph.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val name: String,
    var grade: String
) : Parcelable
