package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class PrescriptionMedicationData(
    val medicineName: String?,
    val strength: String?,
    val dose: String?,
    val route: String?,
    val frequency: String?,
    val refills: String?,
    val indication: String?,
    val additionalDirection: String?,
):Serializable