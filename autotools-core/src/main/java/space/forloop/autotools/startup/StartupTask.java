/* Licensed under Apache-2.0 */
package space.forloop.autotools.startup; /* Licensed under Apache-2.0 */

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import space.forloop.autotools.services.SchedulerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupTask implements ApplicationListener<ApplicationReadyEvent> {

  private final SchedulerService schedulerService;

  @Override
  public void onApplicationEvent(final @NonNull ApplicationReadyEvent event) {
    schedulerService.startAll();
  }
}
