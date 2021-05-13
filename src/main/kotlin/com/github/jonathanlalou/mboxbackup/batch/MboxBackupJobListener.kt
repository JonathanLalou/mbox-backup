package com.github.jonathanlalou.mboxbackup.batch

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

@Component
class MboxBackupJobListener : JobExecutionListener {
    private val LOGGER = KotlinLogging.logger {}
    var fileName: String? = null

    override fun beforeJob(jobExecution: JobExecution) {
        val jobName = jobExecution.jobInstance.jobName
        fileName = jobExecution.jobParameters.getString("fileName")

        println("BeforeJob: $jobName")
        for (fileToDelete in File("./working").listFiles()) {
            LOGGER.info { "Deleting: ${fileToDelete}" }
            fileToDelete.delete()
        }
        for (fileToDelete in File("./done").listFiles()) {
            LOGGER.info { "Deleting: ${fileToDelete}" }
            fileToDelete.delete()
        }
        if (!File("./input/${fileName}").exists()) {
            LOGGER.error { "./input/${fileName} does not exist, will exit" }
            exitProcess(1)
        }
        FileUtils.copyFile(
            File("./input/${fileName}"),
            File("./working/${fileName}")
        )

    }

    override fun afterJob(jobExecution: JobExecution) {
        val jobName = jobExecution.jobInstance.jobName
        println("AfterJob: $jobName")
        try {
            FileUtils.moveFile(
                File("./working/${fileName}"),
                File("./done/${fileName}")
            )
        } catch (e: FileNotFoundException) {
            // nothing to do
        }

    }
}