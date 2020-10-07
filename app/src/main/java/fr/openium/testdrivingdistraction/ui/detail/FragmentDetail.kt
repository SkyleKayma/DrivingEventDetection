package fr.openium.testdrivingdistraction.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.model.TripEvent
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.ext.android.inject


class FragmentDetail : AbstractFragment(R.layout.fragment_detail) {

    private val args: FragmentDetailArgs by navArgs()

    private val model: ViewModelDetail by inject()

    private var gMap: GoogleMap? = null
    private var bounds: LatLngBounds? = null

    private var trip: Trip? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewDetail.onCreate(savedInstanceState)

        trip = model.getSpecificTrip(args.beginDate)

        setToolbar()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        mapViewDetail.onStart()

        mapViewDetail.getMapAsync { map ->
            this.gMap = map

            gMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.maps)
            )

            // Add custom options
            gMap?.uiSettings?.isMapToolbarEnabled = false

            setDetail()
        }
    }

    override fun onResume() {
        super.onResume()
        mapViewDetail.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewDetail.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapViewDetail.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewDetail.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapViewDetail.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewDetail.onSaveInstanceState(outState)
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarDetail as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setListeners() {

    }

    private fun setDetail() {
        textViewDetailNbScreenOnEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.SCREEN_ON.name }?.count()?.toString()
        textViewDetailNbScreenOffEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.SCREEN_OFF.name }?.count()?.toString()
        textViewDetailNbScreenUnlockEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.SCREEN_UNLOCK.name }?.count()?.toString()
        textViewDetailNbReceiveCallEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.RECEIVE_CALL.name }?.count()?.toString()
        textViewDetailNbHookCallEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.HOOK_CALL.name }?.count()?.toString()
        textViewDetailNbNotHookCallEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.NOT_HOOK_CALL.name }?.count()?.toString()
        textViewDetailNbReceiveSMSEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.RECEIVE_SMS.name }?.count()?.toString()
    }
}
