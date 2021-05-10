package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import com.github.jonathanlalou.mboxbackup.helper.MailFormatter
import lombok.extern.log4j.Log4j
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Log4j
class MboxItemWriter : ItemWriter<Mbox> {
    @Autowired
    lateinit var mailFormatter: MailFormatter

    override fun write(items: MutableList<out Mbox>) {
        var i = 0
        var j = 0

        for (mbox in items) {
            println("WARN " + j)
            println("***********************************")
            println("***********************************")
            println("***********************************")
            println("***********************************")
            println("***********************************")
            for (mail in mbox.mails) {
//                FileUtils.write(File("./" + UUID.randomUUID().toString() + ".html"), mail.toString())
                println("INFO " + mailFormatter.format(mail))
                println("ERROR " + i)
                i++
//                println("INFO " + mail.toString())
            }
            j++
        }
    }

}