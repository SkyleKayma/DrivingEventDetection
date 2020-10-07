package fr.openium.testdrivingdistraction.base.viewModel

import androidx.lifecycle.ViewModel
import io.realm.Realm

abstract class AbstractViewModel : ViewModel() {
    protected val realm: Realm = Realm.getDefaultInstance()
}