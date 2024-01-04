package com.app.ecolive.pharmacy_module.model

data class UserPrescriptionData(
    val __v: Int,
    val _id: String,
    val additionalDirections: String,
    val allergies: String,
    val alreadyMedication: String,
    val attachment: String,
    val createdAt: String,
    val doctorId: String,
    val dose: String,
    val frequency: String,
    val habits: String,
    val indication: String,
    val otherRelaventInformation: String,
    val patientId: String,
    val picture: String,
    val quantify: String,
    val recentMedicalHistory: String,
    val refills: String,
    val route: String,
    val sendPrescriptionToPharmacy: Boolean,
    val status: Int,
    val strength: String,
    val symptomDescription: String,
    val symptomDuration: String,
    val updatedAt: String
)