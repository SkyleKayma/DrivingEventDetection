package fr.openium.testdrivingdistraction.enum

import android.hardware.SensorManager
import fr.openium.testdrivingdistraction.R

enum class SensorRate(val rateTextId: Int) {
    NORMAL(R.string.settings_speed_normal_rate),
    UI(R.string.settings_speed_ui_rate),
    GAME(R.string.settings_speed_game_rate),
    FASTEST(R.string.settings_speed_fastest_rate);

    fun getRealSensorRate(): Int =
        when (this) {
            NORMAL ->
                SensorManager.SENSOR_DELAY_NORMAL
            UI ->
                SensorManager.SENSOR_DELAY_UI
            GAME ->
                SensorManager.SENSOR_DELAY_GAME
            FASTEST ->
                SensorManager.SENSOR_DELAY_FASTEST
        }
}