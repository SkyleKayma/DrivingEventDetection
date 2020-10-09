package fr.openium.testdrivingdistraction.ui.detailList

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.ext.navigate
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.ui.detailList.adapter.AdapterDetailList
import fr.openium.testdrivingdistraction.utils.DateUtils
import fr.openium.testdrivingdistraction.utils.ShareUtils
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_detail_list.*
import okio.buffer
import okio.source
import org.koin.android.ext.android.inject


class FragmentDetailList : AbstractFragment(R.layout.fragment_detail_list) {

    private val model: ViewModelDetailList by inject()
    private val dateUtils: DateUtils by inject()
    private val gson: Gson by inject()
    private val shareUtils: ShareUtils by inject()

    private var adapterDetailList: AdapterDetailList? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAdapter()
        setListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_detail_list_import -> {
                importTrip()
                true
            }
            R.id.menu_detail_list_export -> {
                exportTrips()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarDetailList as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setAdapter() {
        adapterDetailList = AdapterDetailList(mutableListOf()) {
            navigate(FragmentDetailListDirections.actionFragmentDetailToFragmentDetail(it))
        }

        recyclerViewDetailList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterDetailList
        }
    }

    private fun setListeners() {
        model.getAllTripsObs().subscribe({
            adapterDetailList?.refreshData(
                it.sortedByDescending { dateUtils.parse(it.beginDate, DateUtils.Format.DATE_FULL)?.time }.toMutableList()
            )

            if (it.isEmpty()) {
                recyclerViewDetailList.gone()
                linearLayoutDetailListEmpty.show()
            } else {
                linearLayoutDetailListEmpty.gone()
                recyclerViewDetailList.show()
            }
        }, {
            Log.e("", "Error getting trips $it")
        }).addTo(disposables)
    }

    private fun importTrip() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/json"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.generic_action_chooser_title)), FILE_SELECT_REQUEST_CODE)
        } catch (ex: ActivityNotFoundException) {
            snackbar(getString(R.string.detail_list_error_no_file_manager), Snackbar.LENGTH_SHORT)
        }
    }

    private fun exportTrips() {
        startActivity(
            Intent.createChooser(
                shareUtils.getExportMultiIntent(getTripsData()),
                getString(R.string.generic_action_chooser_title)
            )
        )
    }

    private fun getTripsData(): List<Pair<String, String>> {
        val trips = model.getAllTrips()

        val data = mutableListOf<Pair<String, String>>()

        trips.forEach {
            data.add(getNameOfFile(it) to getJsonData(it))
        }

        return data
    }

    private fun getJsonData(trip: Trip): String =
        gson.toJson(trip)

    private fun getNameOfFile(trip: Trip): String =
        trip.let {
            "Trip_${trip.beginDate}_${trip.endDate}.json"
        }

    // --- Other Methods
    // ---------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_REQUEST_CODE) {
            data?.data?.also {
                shareUtils.copyFile(it)?.let { validFile ->
                    var textData = ""
                    validFile.source().use { fileSource ->
                        fileSource.buffer().use { bufferedFileSource ->
                            while (true) {
                                val line = bufferedFileSource.readUtf8Line() ?: break
                                textData += line
                            }
                        }
                    }

                    val trip = gson.fromJson(textData, Trip::class.java)
                    model.insertNewTrip(trip)
                }
            }
        }
    }

    companion object {
        private const val FILE_SELECT_REQUEST_CODE = 0x01
    }
}