package com.respondingio.main.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.respondingio.main.R
import com.respondingio.main.RespondDialog
import kotlinx.android.synthetic.main.activity_incident_popup.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class IncidentPopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_popup)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            it.setStyle(Style.Builder().fromUri("mapbox://styles/ahodak/cjk3fznkx1j0o2splunwl0s72"))
            it.addMarker(MarkerOptions().position(LatLng(41.84140052403469, -79.353778577586624)).title("29100 RT 6"))
            it.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(LatLng(41.84140052403469, -79.353778577586624)).zoom(15.0).tilt(0.0).build()))
        }

        respondButton?.onClick {
            //TODO Setup respond button - Click to respond to default (Station), long click to bring up additional options.
        }

        dismiss?.onClick {
            finish()
        }
    }
}

