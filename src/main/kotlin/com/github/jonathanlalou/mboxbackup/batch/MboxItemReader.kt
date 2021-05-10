package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mail
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import lombok.extern.log4j.Log4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import org.apache.commons.lang3.StringUtils
import org.springframework.batch.item.ItemReader
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component


@Log4j
@Component
class MboxItemReader() : ItemReader<Mbox> {
    final val X_GM_THRID = "X-GM-THRID"
    final val X_Gmail_Labels = "X-Gmail-Labels"
    final val X_Gmail_Received = "X-Gmail-Received"
    final val Delivered_To = "Delivered-To"
    final val Received = "Received"
    final val Return_Path = "Return-Path"
    final val Received_SPF = "Received-SPF"
    final val DomainKey_Status = "DomainKey-Status"
    final val DomainKey_Signature = "DomainKey-Signature"
    final val Message_ID = "Message-ID"
    final val Date = "Date"
    final val From = "From"
    final val Subject = "Subject"
    final val To = "To"
    final val In_Reply_To = "In-Reply-To"
    final val MIME_Version = "MIME-Version"
    final val Content_Type = "Content-Type"
    final val Content_Transfer_Encoding = "Content-Transfer-Encoding"
    final val Cc = "Cc"
    final val Bcc = "Bcc"

    val HEADERS = mutableListOf(
        X_GM_THRID,
        X_Gmail_Labels,
        X_Gmail_Received,
        Delivered_To,
        Received,
        Return_Path,
        Received_SPF,
        DomainKey_Status,
        DomainKey_Signature,
        Message_ID,
        Date,
        From,
        Subject,
        To,
        In_Reply_To,
        MIME_Version,
        Content_Type,
        Content_Transfer_Encoding
    )


    override fun read(): Mbox? {
        var mbox = Mbox()
        var file = ClassPathResource("/archives-2005.mbox").file
        val it: LineIterator = FileUtils.lineIterator(file, "UTF-8")
        var lastLine: String?
        lastLine = "\n"
        var regex: Regex = Regex("From (\\d)+@(\\w)+ (\\w){3} (\\w){3} (\\d)+ (\\d)+:(\\d)+:(\\d)+ \\+(\\d)+ (\\d)+")
        var mail: Mail = Mail()
        var inHeaders = true
        var previousHeader = ""
        var previousLine = ""
        var body = ""
        try {
            while (it.hasNext()) {
                val line = it.nextLine()

                if (line.matches(regex)) {
                    // conclude previous mail
                    if (HEADERS.isNotEmpty()) {
                        mail.body = body
//                        mail.from = mail.headers.filter { it.first.equals(From) }. first()?.second
//                        mail.tos = mail.headers.filter { it.first.equals(To) }?.first()?.second.split(",").map { StringUtils.trim(it) }
//                        mail.ccs = mail.headers.filter { it.first.equals(Cc) }?.first()?.second.split(",").map { StringUtils.trim(it) }
//                        mail.bccs = mail.headers.filter { it.first.equals(Bcc) }?.first()?.second.split(",").map { StringUtils.trim(it) }
                        mbox.mails += mail
                    }
                    // init new mail
                    mail = Mail()
                    body = ""
                    mail.headers += Pair(line, "")
                    inHeaders = true
                    continue
                }
                val candidateHeader = StringUtils.substringBefore(line, ":")
                val candidateHeaderValue = StringUtils.substringAfter(line, ":")
                if (inHeaders && HEADERS.contains(candidateHeader.trim())) {
                    mail.headers += Pair(candidateHeader, candidateHeaderValue)
                    previousHeader = candidateHeader
                } else if (
                    Received.equals(previousHeader)
                    || (Content_Type.equals(previousHeader) && StringUtils.contains(line, "charset="))
                ) {
                    inHeaders = true
                    var pair = Pair(Received, mail.headers.last().second + " " + line.trim())
                    mail.headers += pair // TODO replace the last element / delete it before updating
                } else {
                    inHeaders = false
                    body = body + "\n" + line
                    previousLine = line
                }
            }
        } finally {
            LineIterator.closeQuietly(it)
        }
        return mbox;
    }
}