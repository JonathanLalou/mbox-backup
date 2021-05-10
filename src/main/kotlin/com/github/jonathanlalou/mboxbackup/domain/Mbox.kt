package com.github.jonathanlalou.mboxbackup.domain

data class Mbox(
    var label: String? = "",
    var mails: List<Mail> = emptyList()
)