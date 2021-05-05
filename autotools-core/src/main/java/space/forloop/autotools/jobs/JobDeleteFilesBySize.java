/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.ScanService;
import space.forloop.autotools.utils.DeleteUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobDeleteFilesBySize implements UnaryOperator<ScheduledJob> {

  private final ScanService scanService;

  @Override
  public ScheduledJob apply(final ScheduledJob scheduledJob) {

    log.info("Running: {}", this.getClass().getName());

    scanService
        .findFilesFlux(scheduledJob.getSourceDirectory())
        .filter(file -> toDelete(scheduledJob, file))
        .flatMap(DeleteUtils::deleteFile)
        .subscribe();

    log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

    return scheduledJob;
  }

  public boolean toDelete(ScheduledJob scheduledJob, FileWrapper fileWrapper) {
    return fileWrapper.getSize() <= scheduledJob.getLessThanThreshold();
  }
}
