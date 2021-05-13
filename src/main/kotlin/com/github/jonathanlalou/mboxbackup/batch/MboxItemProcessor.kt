package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Bcc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Cc
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.Date
import com.github.jonathanlalou.mboxbackup.batch.MboxItemReader.Companion.To
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.trim
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class MboxItemProcessor : ItemProcessor<Mbox, Mbox> {
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
    val firstLineDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")

    /**
     * Rules:
     * <ul>
     * <li>Search for first empty line</li>
     * <li>anything after is part of the BODY</li>
     * <li>anything before is a header:</li>
     * <ul>
     * <li>if line starts with a blank, then the current line expands the previous header</li>
     * <li>else, this is a new header</li>
     * </ul>
     * </ul>
     */
    override fun process(mbox: Mbox): Mbox? {
        for (mail in mbox.mails) {
            if (mail.raw.equals("")) {
                continue
            }
            val lines = mail.raw.split("\n")
            var pivot = -1
            for (i in 0..(lines.size - 1)) {
                if (lines[i].equals("")) {
                    pivot = i
                    break
                }
            }
            val szHeaders = lines.filterIndexed { index, s -> index < pivot }
            mail.body = lines.filterIndexed { index, s -> index > pivot }.joinToString("\n")

            for (line in szHeaders) {
                if (line.startsWith(" ")) {
                    var last = mail.headers.removeLast()
                    mail.headers.add(Pair(last.first, last.second + "\n" + line))
                } else {
                    val candidateHeader = trim(StringUtils.substringBefore(line, ":"))
                    val candidateHeaderValue = trim(StringUtils.substringAfter(line, ":"))
                    mail.headers.add(Pair(candidateHeader, candidateHeaderValue))
                }
            }

            if (mail.headers.isNotEmpty()) {
                mail.subject = mail.headers
                    .filter { it.first.equals(MboxItemReader.Subject) }
                    .firstOrNull()
                    ?.second
                    ?.substringAfter("<")
                    ?.substringBefore(">")
                    .orEmpty()
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
                        trim(
                            it
                                .substringAfter("<")
                                .substringBefore(">")
                        )
                    }
                mail.ccs = mail.headers
                    .filter { it.first.equals(Cc) }
                    .firstOrNull()?.second
                    ?.split(",")
                    ?.map { trim(it) }
                    .orEmpty()
                mail.bccs = mail.headers
                    .filter { it.first.equals(Bcc) }
                    .firstOrNull()
                    ?.second
                    ?.split(",")
                    ?.map { trim(it) }
                    .orEmpty()

                var szDate = mail.headers.filter { it.first.equals(Date) }.firstOrNull()?.second
                if (null == szDate) {
                    szDate = mail.raw.substringAfter("@xxx ")
                    mail.date = firstLineDateFormat.parse(szDate)
                } else {
                    try {
                        mail.date = dateFormat.parse(szDate)
                    } catch (e: Exception) {
                        szDate = mail.raw.substringAfter("@xxx ")
                        mail.date = firstLineDateFormat.parse(szDate)
                    }
                }
            }
        }
        return mbox
    }
}