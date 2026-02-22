package com.bootlabs.demo.config;

import com.bootlabs.demo.exception.BankApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;


@Slf4j
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate bankApiRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .exponentialBackoff(1000, 2, 10000) // initial, multiplier, max
                .retryOn(BankApiException.class)
                .withListener(new RetryListener() {

                    @Override
                    public <T, E extends Throwable> boolean open(
                            RetryContext context,
                            RetryCallback<T, E> callback) {
                        return true; // allow retry process to start
                    }

                    @Override
                    public <T, E extends Throwable> void onError(
                            RetryContext context,
                            RetryCallback<T, E> callback,
                            Throwable throwable) {

                        log.warn("Retry attempt {} failed: {}",
                                context.getRetryCount(),
                                throwable.getMessage());
                    }

                    @Override
                    public <T, E extends Throwable> void close(
                            RetryContext context,
                            RetryCallback<T, E> callback,
                            Throwable throwable) {
                        // Optional: final logic after retries complete
                    }
                })
                .build();
    }
}