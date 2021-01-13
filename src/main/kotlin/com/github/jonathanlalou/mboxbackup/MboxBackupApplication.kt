package com.github.jonathanlalou.mboxbackup

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MboxBackupApplication

fun main(args: Array<String>) {
	runApplication<MboxBackupApplication>(*args)
}
