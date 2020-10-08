package fr.openium.testdrivingdistraction.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.kotlintools.ext.dip
import fr.openium.kotlintools.ext.getColorCompat
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.model.TripEvent
import fr.openium.testdrivingdistraction.model.TripSensorEvent
import fr.openium.testdrivingdistraction.ui.detail.dialog.DialogTripDelete
import fr.openium.testdrivingdistraction.utils.DateUtils
import fr.openium.testdrivingdistraction.utils.ShareUtils
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.ext.android.inject


class FragmentDetail : AbstractFragment(R.layout.fragment_detail) {

    private val args: FragmentDetailArgs by navArgs()

    private val model: ViewModelDetail by inject()
    private val dateUtils: DateUtils by inject()
    private val gson: Gson by inject()
    private val shareUtils: ShareUtils by inject()

    private var gMap: GoogleMap? = null
    private var bounds: LatLngBounds? = null

    private var trip: Trip? = null

    private var isFirstLaunchDone = false

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

            gMap?.setPadding(0, 0, 0, dip(100))

            setDetail()
            setTripOnMap()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_detail_export -> {
                exportTrip()
                true
            }
            R.id.menu_detail_delete -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarDetail as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setListeners() {
        floatingActionButtonDetailCenterMap.setOnClickListener {
            centerMap()
        }
    }

    private fun setDetail() {
        // Set date
        textViewDetailBeginDate.text =
            dateUtils.parseThenFormat(trip?.beginDate ?: "", DateUtils.Format.DATE_FULL, DateUtils.Format.DATE_ONLY_DATE_TEXT).capitalize()

        // Set time
        textViewDetailBeginTime.text =
            dateUtils.parseThenFormat(trip?.beginDate ?: "", DateUtils.Format.DATE_FULL, DateUtils.Format.DATE_ONLY_TIME_TEXT)

        // Set date
        textViewDetailEndDate.text =
            dateUtils.parseThenFormat(trip?.endDate ?: "", DateUtils.Format.DATE_FULL, DateUtils.Format.DATE_ONLY_DATE_TEXT).capitalize()

        // Set time
        textViewDetailEndTime.text =
            dateUtils.parseThenFormat(trip?.endDate ?: "", DateUtils.Format.DATE_FULL, DateUtils.Format.DATE_ONLY_TIME_TEXT)

        // Set events
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
        textViewDetailNnSendCallEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.SEND_CALL.name }?.count()?.toString()
        textViewDetailNbReceiveSMSEvent.text =
            trip?.events?.filter { it.type == TripEvent.Type.RECEIVE_SMS.name }?.count()?.toString()

        // Set markers
        textViewDetailNbHangUpMarkers.text =
            trip?.sensorEvents?.filter { it.type == TripSensorEvent.Type.HANG_UP.name }?.count()?.toString()

        textViewDetailNbHandUsageMarkers.text =
            trip?.sensorEvents?.filter { it.type == TripSensorEvent.Type.HAND_USAGE.name }?.count()?.toString()

        textViewDetailNbPickUpMarkers.text =
            trip?.sensorEvents?.filter { it.type == TripSensorEvent.Type.PICK_UP.name }?.count()?.toString()
    }

    private fun setTripOnMap() {
        // Clear previous points on gMap
        gMap?.clear()

        // Init boundsBuilder
        val boundsBuilder = LatLngBounds.builder()

        val hasAnyData = showSelectedTripOnMap(boundsBuilder)

        if (hasAnyData) {
            bounds = boundsBuilder.build()
        }

        // Center gMap
        centerMap()
    }

    private fun showSelectedTripOnMap(boundsBuilder: LatLngBounds.Builder): Boolean {
        var hasAnyData = false

        trip?.let {
            val hasTripData = addLocationPoints(boundsBuilder)
            val hasEventData = addEventPoints(boundsBuilder)
            val hasSensorEventData = addSensorEventPoints(boundsBuilder)
            hasAnyData = hasTripData || hasEventData || hasSensorEventData
        }

        return hasAnyData
    }

    private fun centerMap() {
        bounds?.let {
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(it, dip(40))
            if (isFirstLaunchDone) {
                gMap?.animateCamera(cameraUpdate)
            } else {
                gMap?.moveCamera(cameraUpdate)
                isFirstLaunchDone = true
            }
        }
    }

    private fun addLocationPoints(boundsBuilder: LatLngBounds.Builder): Boolean {
        var hasAnyData = false

        trip?.let { validTrip ->
            if (validTrip.locations.isNotEmpty()) {
                // Declare color that will be used
                val color = requireContext().getColorCompat(R.color.colorPrimary)

                // Add all points
                val sortedPoints =
                    validTrip.locations.sortedBy { dateUtils.parse(it.date ?: "", DateUtils.Format.DATE_FULL)?.time }
                val polyline = PolylineOptions()
                sortedPoints.forEach {
                    val latLng = LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)
                    polyline.add(latLng)
                    boundsBuilder.include(latLng)
                }

                // Add start marker
                sortedPoints.first().let { first ->
                    val latLng = LatLng(first.latitude ?: 0.0, first.longitude ?: 0.0)
                    boundsBuilder.include(latLng)

                    // This is needed to prevent "no included points" error
                    hasAnyData = true

                    gMap?.addMarker(
                        MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.from_map_indicator_resgen))
                            .anchor(0.5f, 0.5f)
                    )
                }

                // Add end marker
                sortedPoints.last().let { last ->
                    val latLng = LatLng(last.latitude ?: 0.0, last.longitude ?: 0.0)
                    boundsBuilder.include(latLng)

                    // This is needed to prevent "no included points" error
                    hasAnyData = true

                    gMap?.addMarker(
                        MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.to_map_indicator_resgen))
                            .anchor(0.5f, 0.5f)
                    )
                }

                // Customize the polyline
                polyline.color(color)
                polyline.width(dip(4f).toFloat())

                gMap?.addPolyline(polyline)
            }
        }

        return hasAnyData
    }

    private fun addEventPoints(boundsBuilder: LatLngBounds.Builder): Boolean {
        var hasData = false

        trip?.let { validTrip ->
            for (event in validTrip.events.filter { it.latitude != null && it.longitude != null }) {
                val latLng = LatLng(event.latitude ?: 0.0, event.longitude ?: 0.0)
                boundsBuilder.include(latLng)

                // This is needed to prevent "no included points" error
                hasData = true

                gMap?.addMarker(
                    MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(TripEvent.Type.fromString(event.type).iconId))
                        .anchor(0.5f, 0.85f)
                )
            }
        }

        return hasData
    }

    private fun addSensorEventPoints(boundsBuilder: LatLngBounds.Builder): Boolean {
        var hasData = false

        trip?.let { validTrip ->
            for (event in validTrip.sensorEvents.filter { it.latitude != null && it.longitude != null }) {
                val latLng = LatLng(event.latitude ?: 0.0, event.longitude ?: 0.0)
                boundsBuilder.include(latLng)

                // This is needed to prevent "no included points" error
                hasData = true

                gMap?.addMarker(
                    MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(TripSensorEvent.Type.fromString(event.type).iconId))
                        .anchor(0.5f, 0.85f)
                )
            }
        }

        return hasData
    }

    private fun exportTrip() {
        startActivity(
            Intent.createChooser(
                shareUtils.getExportSingleIntent(getNameOfFile(), getJsonData()),
                getString(R.string.generic_action_chooser_title)
            )
        )
    }

    private fun getJsonData(): String =
        gson.toJson(trip)

    private fun getNameOfFile(): String =
        trip?.let {
            "Trip_${trip?.beginDate}_${trip?.endDate}.json"
        } ?: "Trip_UNKNOWN.json"

    private fun showDeleteDialog() {
        DialogTripDelete().apply {
            setListener(object : DialogTripDelete.OnPositiveButtonClickedListener {
                override fun onPositiveButtonClicked() {
                    trip?.let {
                        model.deleteTrip(it)
                        findNavController().navigateUp()
                    }
                }
            })
        }.show(childFragmentManager, "DialogBudgetDelete")
    }
}