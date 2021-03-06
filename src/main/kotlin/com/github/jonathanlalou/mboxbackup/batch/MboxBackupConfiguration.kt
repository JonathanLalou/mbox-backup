package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MboxBackupConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun step1(
        mboxItemReader: MboxItemReader,
        mboxItemReaderListener: MboxItemReaderListener,
        mboxItemProcessor: MboxItemProcessor,
        mboxItemWriter: MboxItemWriter
    ): Step {
        return stepBuilderFactory.get("step1")
            .chunk<Mbox, Mbox>(1)
            .reader(mboxItemReader)
            .listener(mboxItemReaderListener)
            .processor(mboxItemProcessor)
            .writer(mboxItemWriter)
            .build()
    }

    @Bean
    fun job(
        jobListener: MboxBackupJobListener,
        mboxItemReader: MboxItemReader,
        mboxItemReaderListener: MboxItemReaderListener,
        mboxItemProcessor: MboxItemProcessor,
        mboxItemWriter: MboxItemWriter
    ): Job {
        return jobBuilderFactory.get("job")
            .listener(jobListener)
            .start(step1(mboxItemReader, mboxItemReaderListener, mboxItemProcessor, mboxItemWriter))
            .build()
    }
}