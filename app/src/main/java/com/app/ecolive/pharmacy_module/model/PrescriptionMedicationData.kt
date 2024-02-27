package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class PrescriptionMedicationData(
    val additionalDirections: String?,
    val dose: String?,
    val frequency: String?,
    val indication: String?,
    val medicineName: String?,
    val refills: String?,
    val route: String?,
    val strength: String?
):Serializable