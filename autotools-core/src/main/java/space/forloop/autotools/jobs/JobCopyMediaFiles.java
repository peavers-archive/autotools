/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.HistoryService;
import space.forloop.autotools.services.ScanService;
import space.forloop.autotools.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCopyMediaFiles implements UnaryOperator<ScheduledJob> {

    private final ScanService scanService;

    private final HistoryService historyService;

    private static final long WAIT_DURATION = 10;

    @Override
    public ScheduledJob apply(final ScheduledJob scheduledJob) {

        scanService
                .findFilesFlux(scheduledJob.getSourceDirectory())
                .filter(f -> !historyService.isAlreadyProcessed(f.getId()))
                .filter(checkIgnored(scheduledJob))
                .filter(checkAlreadyExists(scheduledJob))
                .doOnNext(fileWrapper -> copyFile(fileWrapper, scheduledJob.getTargetDirectory()))
                .subscribe();

        log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

        return scheduledJob;
    }

    protected Predicate<FileWrapper> checkAlreadyExists(final ScheduledJob scheduledJob) {

        return file ->
                FileUtils.isMediaFile(Path.of(file.getPath()))
                        && !Files.exists(Path.of(scheduledJob.getTargetDirectory(), file.getPath()));
    }

    protected Predicate<FileWrapper> checkIgnored(ScheduledJob scheduledJob) {

        final Splitter splitter = Splitter.on(',').trimResults().omitEmptyStrings();

        final ArrayList<String> list =
                Lists.newArrayList(splitter.split(scheduledJob.getIgnoreWords()));

        return file -> list.stream().noneMatch(file.getPath()::contains);
    }

    private void copyFile(final FileWrapper fileWrapper, final String copyFilesTo) {

        final File file = new File(fileWrapper.getPath());
        final File target = new File(copyFilesTo);

        try {
            waitForFile(file);

            org.apache.commons.io.FileUtils.copyFileToDirectory(file, target);

            log.info("Copied {} to {}", file.getName(), target.getAbsolutePath());
        } catch (final IOException | InterruptedException e) {
            log.error("Unable to copy file {}", e.getMessage());
        } finally {
            historyService.saveFileWrapper(fileWrapper);
        }
    }

    /**
     * A bit hacky, but we want to make sure we don't copy files that are in the process of being copied to the
     * target folder. For example if we're downloading a file into /downloads - don't start copying the file to
     * /complete until the size of the file stops growing.
     * <p>
     * We're assuming that if the filesize doesn't change in the time specified in
     * {@link JobCopyMediaFiles#WAIT_DURATION} that it must be done downloading.
     */
    protected void waitForFile(final File file) throws IOException, InterruptedException {

        log.info("Waiting for file: {}", file.getAbsolutePath());

        long lastCheckedSize;

        do {
            lastCheckedSize = Files.size(Path.of(file.getPath()));

            TimeUnit.SECONDS.sleep(WAIT_DURATION);

        } while (lastCheckedSize != Files.size(Path.of(file.getPath())));

        log.info("Done waiting for file: {}", file.getAbsolutePath());
    }

}
