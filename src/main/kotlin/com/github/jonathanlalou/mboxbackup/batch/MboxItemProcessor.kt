package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Bcc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Cc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.To
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import org.apache.commons.lang3.StringUtils
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class MboxItemProcessor : ItemProcessor<Mbox, Mbox> {
    override fun process(mbox: Mbox): Mbox? {
        for (mail in mbox.mails) {
            if (mail.headers.isNotEmpty()) {
                mail.subject = mail.headers
                    .filter { it.first.equals(MboxItemReader.Subject) }
                    .firstOrNull()
                    ?.second
                    ?.substringAfter("<")
                    ?.substringBefore(">")
                mail.from = mail.headers
                    .filter { it.first.equals(MboxItemReader.From) }
                    .firstOrNull()
                    ?.second
                    ?.substringAfter("<")
                    ?.substringBefore(">")
                mail.tos = mail.headers
                    .filter { it.first.equals(To) }
                    .firstOrNull()?.second
                    ?.split(",")
                    ?.map {
                        StringUtils.trim(
                            it
                                .substringAfter("<")
                                .substringBefore(">")
                        )
                    }
                mail.ccs = mail.headers
                    .filter { it.first.equals(Cc) }
                    .firstOrNull()?.second
                    ?.split(",")
                    ?.map { StringUtils.trim(it) }
                mail.bccs = mail.headers
                    .filter { it.first.equals(Bcc) }
                    .firstOrNull()
                    ?.second
                    ?.split(",")
                    ?.map { StringUtils.trim(it) }
            }
        }
        return mbox
    }
}