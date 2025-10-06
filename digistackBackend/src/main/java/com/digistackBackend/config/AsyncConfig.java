package com.digistackBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "asyncExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(20);
        delegate.setMaxPoolSize(50);
        delegate.setQueueCapacity(200);
        delegate.setThreadNamePrefix("keyword-worker-");
        delegate.initialize();

        // ✅ Properly wraps executor to propagate SecurityContext to async threads
        return new DelegatingSecurityContextExecutor(delegate);
    }
}
