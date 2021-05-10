package com.github.jonathanlalou.mboxbackup

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class MboxBackupApplication

fun main(args: Array<String>) {
	runApplication<MboxBackupApplication>(*args)
}
