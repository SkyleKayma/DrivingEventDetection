package fr.openium.testdrivingdistraction.ui.detailList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.ext.navigate
import fr.openium.testdrivingdistraction.ui.detailList.adapter.AdapterDetailList
import kotlinx.android.synthetic.main.fragment_detail_list.*
import org.koin.android.ext.android.inject


class FragmentDetailList : AbstractFragment(R.layout.fragment_detail_list) {

    private val model: ViewModelDetailList by inject()

    private var adapterDetailList: AdapterDetailList? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAdapter()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarDetailList as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setAdapter() {
        adapterDetailList = AdapterDetailList(model.getAllTrips()) {
            navigate(FragmentDetailListDirections.actionFragmentDetailToFragmentDetail(it))
        }

        recyclerViewDetail.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterDetailList
        }
    }
}
