package fr.openium.testdrivingdistraction.base.fragment

import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractFragment(@LayoutRes open val layoutId: Int) : Fragment(layoutId) {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == android.R.id.home) {
            findNavController().popBackStack()
            true
        } else super.onOptionsItemSelected(item)

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}