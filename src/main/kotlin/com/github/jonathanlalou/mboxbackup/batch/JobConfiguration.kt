package com.github.jonathanlalou.mboxbackup.batch

import com.github.jonathanlalou.mboxbackup.domain.Mbox
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun step1(mboxItemReader: MboxItemReader, mboxItemWriter: MboxItemWriter): Step {
        return stepBuilderFactory.get("step1")
            .chunk<Mbox, Mbox>(10)
            .reader(mboxItemReader)
            .writer(mboxItemWriter)
            .build()
    }

    @Bean
    fun job(mboxItemReader: MboxItemReader, mboxItemWriter: MboxItemWriter): Job {
        return jobBuilderFactory.get("job")
            .start(step1(mboxItemReader, mboxItemWriter))
            .build()
    }
}