package com.respondingio.main.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.respondingio.main.R
import com.respondingio.main.RespondDialog
import com.respondingio.main.models.NotificationIncident
import kotlinx.android.synthetic.main.activity_incident_popup.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class IncidentPopupActivity : AppCompatActivity() {

    lateinit var map: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_popup)

        val incident = intent.getSerializableExtra("incident") as? NotificationIncident
        if (incident != null) update(incident)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            map = it
            it.setStyle(Style.Builder().fromUri("mapbox://styles/ahodak/cjk3fznkx1j0o2splunwl0s72"))

            if (incident?.location != null) {
                map.addMarker(MarkerOptions().position(incident.location?.coordinates?.toLatLng()).title(incident.location?.fromText))
                map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(incident.location?.coordinates?.toLatLng()).zoom(16.0).tilt(0.0).build()))
            }
        }

        respondButton?.onClick {
            //TODO Setup respond button - Click to respond to default (Station), long click to bring up additional options.
        }

        dismiss?.onClick {
            finish()
        }
    }

    private fun update(incident: NotificationIncident) {
        incidentTitle?.text = incident.callType
        incidentAddress?.text = incident.location?.fromText
        incidentText?.text = incident.initialText

        imageView3.setImageDrawable(resources.getDrawable(incident.getIcon()))

        alertLayout?.setBackgroundColor(resources.getColor(incident.getColor()))
        incidentColor?.setBackgroundColor(resources.getColor(incident.getColor()))
    }
}

