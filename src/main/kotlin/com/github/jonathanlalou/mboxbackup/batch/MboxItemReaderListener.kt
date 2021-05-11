package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.springframework.batch.core.ItemReadListener
import org.springframework.stereotype.Component
import java.io.File

@Component
class MboxItemReaderListener : ItemReadListener<Mbox> {
    private val LOGGER = KotlinLogging.logger {}
    override fun beforeRead() {
        LOGGER.info { "before read" }
    }

    override fun afterRead(mbox: Mbox) {
        LOGGER.info { "After reading: ${mbox.label}" }
        FileUtils.moveFile(
            File("./working/${mbox.label}"),
            File("./done/${mbox.label}")
        )

    }

    override fun onReadError(exception: Exception) {
        LOGGER.error { exception }
    }
}