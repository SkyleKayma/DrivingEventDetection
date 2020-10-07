package fr.openium.testdrivingdistraction.ui.main

import android.os.Bundle
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.activity.AbstractActivity

class ActivityMain : AbstractActivity() {

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // --- Methods
    // ---------------------------------------------------
}