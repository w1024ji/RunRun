package com.example.runrun

data class NotificationItem @JvmOverloads constructor(
    val notiNm: String = "",
    val busNm: String = "",
    val staNm: String = "",
    val selectedDays: String = "",
    val whenToWhen: String = "",
    var docId: String? = null,
)

