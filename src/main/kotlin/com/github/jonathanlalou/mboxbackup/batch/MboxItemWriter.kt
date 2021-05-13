package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import com.github.jonathanlalou.mboxbackup.helper.MailFormatter
import lombok.extern.log4j.Log4j
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat

@Component
@Log4j
class MboxItemWriter : ItemWriter<Mbox> {
    private val LOGGER = KotlinLogging.logger {}

    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")

    @Autowired
    lateinit var mailFormatter: MailFormatter

    override fun write(items: MutableList<out Mbox>) {
        for (mbox in items) {
            Files.createDirectories(Paths.get("./output/${mbox.label}/raw"))
            for (mail in mbox.mails) {
                if (mail.date != null) {
                    LOGGER.info { "Writing... ${mail.raw.subSequence(0..59)}" }
                    var fileName = dateFormat.format(mail.date) +
                            "_" +
                            mail.subject
                                ?.replace(" ", "_")
                                ?.replace("!", "")
                                ?.replace(":", "")
                                ?.replace(".", "_")
                                ?.replace(",", "_")
                                ?.replace("@", "")
                                ?.replace("?", "")
                                ?.replace("=", "_")
                                ?.replace(";", "")
                                ?.replace("'", "")
                                ?.replace("\"", "_")
                                ?.replace("\n", "")
                                ?.replace("\r", "")
                                ?.replace("\t", "")
                                ?.replace("\\p{Punct}", "")
                                ?.replace("\\P{Alnum}", "")
                    FileUtils.write(
                        File("./output/${mbox.label}/${fileName}.html.html"),
                        mailFormatter.format(mail),
                        Charset.defaultCharset()
                    )
                    FileUtils.write(
                        File("./output/${mbox.label}/raw/${fileName}.txt.txt"),
                        mail.raw,
                        Charset.defaultCharset()
                    )
                }
            }
        }
    }

}