package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mail
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import lombok.extern.log4j.Log4j
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.startsWith
import org.apache.commons.lang3.StringUtils.trim
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.io.File


@Log4j
@Component
class MboxItemReader : ItemReader<Mbox> {
    companion object {
        const val X_GM_THRID = "X-GM-THRID"
        const val X_Gmail_Labels = "X-Gmail-Labels"
        const val X_Gmail_Received = "X-Gmail-Received"
        const val Delivered_To = "Delivered-To"
        const val Received = "Received"
        const val Return_Path = "Return-Path"
        const val Received_SPF = "Received-SPF"
        const val DomainKey_Status = "DomainKey-Status"
        const val DomainKey_Signature = "DomainKey-Signature"
        const val Message_ID = "Message-ID"
        const val Date = "Date"
        const val From = "From"
        const val Subject = "Subject"
        const val To = "To"
        const val In_Reply_To = "In-Reply-To"
        const val MIME_Version = "MIME-Version"
        const val Content_Type = "Content-Type"
        const val Content_Transfer_Encoding = "Content-Transfer-Encoding"
        const val Cc = "Cc"
        const val Bcc = "Bcc"
    }

    private val LOGGER = KotlinLogging.logger {}

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
        Cc,
        Bcc,
        In_Reply_To,
        MIME_Version,
        Content_Type,
        Content_Transfer_Encoding
    )

    var fileName: String? = null

    @Suppress("unused")
    @BeforeStep
    fun beforeStep(stepExecution: StepExecution) {
        val jobParameters: JobParameters = stepExecution.jobParameters
        fileName = jobParameters.getString("fileName")
    }

    override fun read(): Mbox? {
        var mbox = Mbox()
        mbox.label = fileName
        var file = File("./working/" + fileName)
        if (!file.exists()) {
            return null
        }
        val it: LineIterator = FileUtils.lineIterator(file, "UTF-8")
        var lastLine: String?
        lastLine = "\n"
        var regex: Regex = Regex("From (\\d)+@(\\w)+ (\\w){3} (\\w){3} (\\d)+ (\\d)+:(\\d)+:(\\d)+ \\+(\\d)+ (\\d)+")
        var mail: Mail = Mail()
        var inHeaders = true
        var previousHeader = ""
        var previousLine = ""
        var body = ""
        var raw = ""
        try {
            while (it.hasNext()) {
                val line = it.nextLine()

                if (line.matches(regex)) {
                    // conclude previous mail
                    mail.body = body
                    mail.raw = raw
                    mbox.mails += mail
                    LOGGER.info { "(current size: ${mbox.mails.size}) Processing new mail element: ${line}" }
                    // init new mail
                    mail = Mail()
                    body = ""
                    raw = line
                    mail.headers += Pair(line, "")
                    inHeaders = true
                    continue
                }
                raw = raw + "\n" + line
                val candidateHeader = trim(StringUtils.substringBefore(line, ":"))
                val candidateHeaderValue = trim(StringUtils.substringAfter(line, ":"))
                if (inHeaders && HEADERS.contains(candidateHeader.trim())) {
                    mail.headers += Pair(candidateHeader, candidateHeaderValue)
                    previousHeader = candidateHeader
                } else if (
                    Received.equals(previousHeader)
                    || (Content_Type.equals(previousHeader) && StringUtils.contains(line, "charset="))
                ) {
                    inHeaders = true
                    var pair = Pair(previousHeader, mail.headers.last().second + " " + line.trim())
                    mail.headers.removeLast()
                    mail.headers += pair
                    // don't update `previousHeader`
                } else if (
                    DomainKey_Signature.equals(previousHeader)
                    && startsWith(line, " ")
                    && (startsWith(trim(line), "s=") || startsWith(trim(line), "h=") || startsWith(trim(line), "b="))
                ) {
                    inHeaders = true
                    var pair = Pair(DomainKey_Signature, mail.headers.last().second + " " + line.trim())
                    mail.headers.removeLast()
                    mail.headers += pair
                    // don't update `previousHeader`
                } else {
                    inHeaders = false
                    body = body + "\n" + line
                    previousLine = line
                }
            }
        } finally {
            LineIterator.closeQuietly(it)
        }
        return mbox
    }
}