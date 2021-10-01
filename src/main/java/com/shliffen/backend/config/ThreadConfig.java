package com.shliffen.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Making a configuration for booking deliveries in parallel. Used ThreadPoolTaskExecutor() with base size of 2
 * Threads and maximal as 10 threads in parallel.
 */
@Configuration
public class ThreadConfig {

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }

}
