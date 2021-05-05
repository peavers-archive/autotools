/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import com.google.common.collect.Iterables;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.DuplicateAdvance;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.HashService;
import space.forloop.autotools.services.ScanService;
import space.forloop.autotools.services.ThumbnailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobDuplicateMediaAdvance implements UnaryOperator<ScheduledJob> {

  private final ScanService scanService;

  private final ThumbnailService thumbnailService;

  private final HashService hashService;

  @Override
  public ScheduledJob apply(final ScheduledJob scheduledJob) {

    log.info("Running: {}", this.getClass().getName());

    getFiles(scheduledJob)
        .map(this::buildDuplicate)
        .collect(Collectors.groupingBy(DuplicateAdvance::getHash))
        .values()
        .parallelStream()
        .filter(duplicateAdvances -> duplicateAdvances.size() > 1)
        .forEach(deleteAllButLargestFile());

    log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

    return scheduledJob;
  }

  private Stream<FileWrapper> getFiles(ScheduledJob scheduledJob) {

    return scanService.findFiles(scheduledJob.getSourceDirectory());
  }

  private DuplicateAdvance buildDuplicate(final FileWrapper fileWrapper) {

    final Path path = thumbnailService.create(fileWrapper);
    final long hash = hashService.getPerceptualHash(path.toFile());

    return DuplicateAdvance.builder().path(path).hash(hash).fileWrapper(fileWrapper).build();
  }

  private Consumer<List<DuplicateAdvance>> deleteAllButLargestFile() {
    return duplicates -> {
      duplicates.sort(Comparator.naturalOrder());

      for (final DuplicateAdvance duplicateAdvance : Iterables.skip(duplicates, 1)) {
        FileUtils.deleteQuietly(duplicateAdvance.getPath().toFile());
        log.info("Deleted duplicate: {}", duplicateAdvance.getPath());
      }
    };
  }
}
