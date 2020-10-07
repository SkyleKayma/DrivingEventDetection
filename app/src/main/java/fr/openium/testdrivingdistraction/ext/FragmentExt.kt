package fr.openium.testdrivingdistraction.ext

import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import fr.openium.kotlintools.ext.getColorCompat

fun Fragment.getColorStateListFromColor(colorId: Int): ColorStateList =
    ColorStateList.valueOf(requireContext().getColorCompat(colorId))

fun Fragment.navigate(navDirections: NavDirections, navOptions: NavOptions? = null) {
    val finalNavOptions = navOptions ?: NavOptions.Builder()
        .build()

    findNavController().navigate(navDirections, finalNavOptions)
}