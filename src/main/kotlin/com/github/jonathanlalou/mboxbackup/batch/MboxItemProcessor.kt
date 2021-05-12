package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Bcc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Cc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Date
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.To
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import org.apache.commons.lang3.StringUtils
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class MboxItemProcessor : ItemProcessor<Mbox, Mbox> {
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
    val firstLineDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")

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

                var szDate = mail.headers.filter { it.first.equals(Date) }.firstOrNull()?.second
                if (null == szDate) {
                    szDate = mail.raw?.substringAfter("@xxx ")
                    mail.date = firstLineDateFormat.parse(szDate)
                } else {
                    try {
                        mail.date = dateFormat.parse(szDate)
                    } catch (e: Exception) {
                        szDate = mail.raw?.substringAfter("@xxx ")
                        mail.date = firstLineDateFormat.parse(szDate)
                    }
                }
            }
        }
        return mbox
    }
}