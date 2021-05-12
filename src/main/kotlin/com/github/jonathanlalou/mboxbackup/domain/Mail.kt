package com.github.jonathanlalou.mboxbackup.domain

import java.util.*

data class Mail(
    var date: Date? = null,

    var from: String? = null,
    var tos: List<String>? = emptyList(),
    var ccs: List<String>? = emptyList(),
    var bccs: List<String>? = emptyList(),
    var headers: MutableList<Pair<String, String>> = mutableListOf(),

    var subject: String? = null,
    var body: String? = null,

    var raw: String = "",

    // TODO attachments
)