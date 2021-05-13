package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mail
import com.github.jonathanlalou.mboxbackup.domain.Mbox
import lombok.extern.log4j.Log4j
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
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
        const val ARC_Seal = "ARC-Seal"
        const val ARC_Message_Signature = "ARC-Message-Signature"
        const val ARC_Authentication_Results = "ARC-Authentication-Results"
        const val Authentication_Results = "Authentication-Results"
        const val DKIM_Signature = "DKIM-Signature"
        const val X_Google_DKIM_Signature = "X-Google-DKIM-Signature"
        const val X_Gm_Message_State = "X-Gm-Message-State"
        const val X_Google_Smtp_Source = "X-Google-Smtp-Source"
        const val References = "References"
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
        Content_Transfer_Encoding,
        ARC_Seal,
        ARC_Message_Signature,
        ARC_Authentication_Results,
        Authentication_Results,
        DKIM_Signature,
        X_Google_DKIM_Signature,
        X_Gm_Message_State,
        X_Google_Smtp_Source,
        References,
    )

    var fileName: String? = null

    @Suppress("unused")
    @BeforeStep
    fun beforeStep(stepExecution: StepExecution) {
        val jobParameters: JobParameters = stepExecution.jobParameters
        fileName = jobParameters.getString("fileName")
    }

    override fun read(): Mbox? {
        val mbox = Mbox()
        mbox.label = fileName
        val file = File("./working/" + fileName)
        if (!file.exists()) {
            return null
        }
        val it: LineIterator = FileUtils.lineIterator(file, "UTF-8")
        val regex = Regex("From (\\d)+@(\\w)+ (\\w){3} (\\w){3} (\\d)+ (\\d)+:(\\d)+:(\\d)+ \\+(\\d)+ (\\d)+")
        var mail = Mail()
        var body = ""
        var raw = ""
        try {
            while (it.hasNext()) {
                val line = it.nextLine()

                if (line.length > 4
                    && line.subSequence(0, 4).equals("From")
                    && line.matches(regex)
                ) {
                    // conclude previous mail
                    mail.body = body
                    mail.raw = raw
                    mbox.mails += mail
                    LOGGER.info { "(current size: ${mbox.mails.size}) Processing new mail element: ${line}" }
                    // init new mail
                    mail = Mail()
                    body = ""
                    raw = line
                    continue
                }
                raw = raw + "\n" + line
            }
        } finally {
            LineIterator.closeQuietly(it)
        }
        return mbox
    }
}