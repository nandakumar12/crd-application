package com.nandakumar12.crd.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * This is the configuration class for the current application
 * few beans have been defined here, which will be managed by spring
 * and we can inject those in other beans when needed
 *
 * @author  Nandakumar12
 */
@Configuration
public class AppConfig {
  /**
   * This is an method for multi-thread configuration

   * @return Executor This method returns a executor which then can be used along
   *                  with @Async to provide the thread configuration for the
   *                  annotated method
   *
   */
  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(25);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("crd-thread-");
    executor.initialize();
    return executor;
  }
}
