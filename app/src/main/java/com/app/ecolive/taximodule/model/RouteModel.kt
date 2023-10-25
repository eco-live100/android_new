package com.app.ecolive.taximodule.model;

import java.io.Serializable;

class RouteModel : Serializable {
    var distance: String? = null
    var duration: String? = null
    var valueDistance: String? = null
    var selectedRoute: String? = null
    var valueDuration: String? = null

    companion object {
        private const val serialVersionUID = 1L
    }
}
