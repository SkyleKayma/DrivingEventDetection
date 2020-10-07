package fr.openium.testdrivingdistraction.ext

import android.content.Context
import android.content.res.ColorStateList
import fr.openium.kotlintools.ext.getColorCompat

fun Context.getColorStateListFromColor(colorId: Int) =
    ColorStateList.valueOf(this.getColorCompat(colorId))