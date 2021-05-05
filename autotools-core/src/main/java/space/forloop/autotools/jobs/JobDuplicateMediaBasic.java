/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import com.google.common.collect.Iterables;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.Duplicate;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.ScanService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobDuplicateMediaBasic implements UnaryOperator<ScheduledJob> {

  private final ScanService scanService;

  @Override
  public ScheduledJob apply(final ScheduledJob scheduledJob) {
    log.info("Running: {}", this.getClass().getName());

    getFiles(scheduledJob).collect(Collectors.groupingBy(FileWrapper::getSize)).entrySet().stream()
        .filter(entry -> entry.getValue().size() > 1)
        .map(buildDuplicate())
        .forEach(deleteAllButLastAccessed());

    log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

    return scheduledJob;
  }

  private Stream<FileWrapper> getFiles(ScheduledJob scheduledJob) {

    return scanService.findFiles(scheduledJob.getSourceDirectory());
  }

  private Function<Map.Entry<Long, List<FileWrapper>>, Duplicate> buildDuplicate() {

    return entry -> Duplicate.builder().id(entry.getKey()).duplicates(entry.getValue()).build();
  }

  private Consumer<Duplicate> deleteAllButLastAccessed() {

    return duplicate -> {
      duplicate.getDuplicates().sort(Comparator.comparing(FileWrapper::getLastAccessTime).reversed());

      for (final FileWrapper fileWrapper : Iterables.skip(duplicate.getDuplicates(), 1)) {
        FileUtils.deleteQuietly(fileWrapper.toNative());
        log.info("Deleted duplicate: {}", fileWrapper.getPath());
      }
    };
  }
}
