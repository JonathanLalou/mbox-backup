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
                    <html>
                        <head></head>
                        <body>
                            ${headers}
                            <br/>
                            <br/>
                            <h1>${mail.subject}</h1>
                            <br/>
                            <br/>
                            ${mail.body}
                        </body>
                    </html>
                """
    }
}