# mbox-backup

Yet another backupper for GMail's mboxes.

## Context

Google hosted documents at their format for free, but decided to stop this policy starting June 2021. Therefore, any document until this date will remain free ;-)

## Use

* Clone this repository.
* Export MBoxes from Google Takeout (https://takeout.google.com/)
* Place the mboxes in `input` folder
* From command line: `mvn spring-boot:run -Dspring-boot.run.arguments="fileName=<file_name_to_import>.mbox`
  eg:

```
mvn spring-boot:run -Dspring-boot.run.arguments="fileName=kids-SummerCamp.mbox"
```

* in the end in `output` folder there will be HTML files, as well as a `raw` folder which contains the text version of the emails.

## Technical

This project was made with Kotlin 1.5. At runtime, the memory print is limited, as imported files are parsed line per line (avoids loading the JVM with a 1GB file in memory).

## FAQ

> Why doing this in Kotlin and SpringBoot? It could have been done easily in Python/Ruby/etc./!

Yes, I could have done the same with a basic Groovy script in some minutes.

Actually, this project was a pretext for me to play with Kotlin and SpringBoot, as I have not had the occasion to use them in a professional context for the two last years.

> The backup is incomplete and the generated HTML is awful!

Yes. I'd like to improve the design and process properly the attachments.