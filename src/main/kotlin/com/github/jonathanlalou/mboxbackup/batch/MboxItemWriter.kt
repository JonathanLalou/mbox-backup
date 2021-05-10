package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import lombok.extern.log4j.Log4j
import org.apache.commons.io.FileUtils
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

@Component
@Log4j
class MboxItemWriter : ItemWriter<Mbox> {
    override fun write(items: MutableList<out Mbox>) {
        for (mbox in items) {
            for (mail in mbox.mails) {
//                FileUtils.write(File("./" + UUID.randomUUID().toString() + ".html"), mail.toString())
                println("INFO " + mail.toString())
            }
        }
    }

}