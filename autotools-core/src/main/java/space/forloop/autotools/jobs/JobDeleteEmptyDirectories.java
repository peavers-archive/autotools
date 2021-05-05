/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.Directory;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.ScanService;
import space.forloop.autotools.utils.DeleteUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobDeleteEmptyDirectories implements UnaryOperator<ScheduledJob> {

  private static final int ZERO = 0;

  private final ScanService scanService;

  @Override
  public ScheduledJob apply(final ScheduledJob scheduledJob) {

    log.info("Running: {}", this.getClass().getName());

    scanService
        .findDirectoriesFlux(scheduledJob.getSourceDirectory())
        .filter(this::toDelete)
        .flatMap(DeleteUtils::deleteDirectory)
        .subscribe();

    log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

    return scheduledJob;
  }

  public boolean toDelete(Directory directory) {
    return directory.getChildren() <= ZERO;
  }
}
