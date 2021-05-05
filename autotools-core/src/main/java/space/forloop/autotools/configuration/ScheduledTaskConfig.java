/* Licensed under Apache-2.0 */
package space.forloop.autotools.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@RequiredArgsConstructor
public class ScheduledTaskConfig {

  private static final int CONCURRENT_JOBS = 2;

  @Bean(name = "taskScheduler")
  public ScheduledExecutorService taskScheduler() {

    return Executors.newScheduledThreadPool(CONCURRENT_JOBS);
  }
}
