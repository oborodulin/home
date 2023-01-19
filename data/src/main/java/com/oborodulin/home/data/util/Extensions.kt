package com.oborodulin.home.data.util

import android.content.Context
import com.oborodulin.home.data.local.db.HomeDatabase

fun Context.dbVersion() =
    HomeDatabase.getInstance(this).openHelper.readableDatabase?.version.toString()