package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import com.github.jonathanlalou.mboxbackup.helper.MailFormatter
import lombok.extern.log4j.Log4j
import mu.KotlinLogging
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Log4j
class MboxItemWriter : ItemWriter<Mbox> {
    private val LOGGER = KotlinLogging.logger {}

    @Autowired
    lateinit var mailFormatter: MailFormatter

    override fun write(items: MutableList<out Mbox>) {
        for (mbox in items) {
            for (mail in mbox.mails) {
//                FileUtils.write(File("./" + UUID.randomUUID().toString() + ".html"), mail.toString())
                LOGGER.info { mailFormatter.format(mail) }
            }
        }
    }

}