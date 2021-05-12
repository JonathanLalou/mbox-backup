package com.github.jonathanlalou.mboxbackup.helper

import com.github.jonathanlalou.mboxbackup.domain.Mail
import org.springframework.stereotype.Component

@Component
class MailFormatter {
    fun format(mail: Mail): String {
        var headers = "<table>\n"
        for (header in mail.headers) {
            headers += "<tr><td><code>${header.first}</code></td><td><code>${header.second}</code></td></tr>\n"
        }

        headers += "</table>"
//        TODO add from, date, to, cc, bcc
        return """
                <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
                    <html>
                        <head></head>
                        <body>
                            ${headers}
                            <br/>
                            <br/>
                            <h2>From: ${mail.from}</h2>
                            <h2>To: ${mail.tos?.joinToString()}</h2>
                            <h2>Cc: ${mail.ccs?.joinToString()}</h2>
                            <h2>Bcc: ${mail.bccs?.joinToString()}</h2>
                            <h1>${mail.subject}</h1>
                            <br/>
                            <br/>
                            ${mail.body}
                        </body>
                    </html>
                """
    }
}