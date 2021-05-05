/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import com.github.junrar.ContentDescription;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.HistoryService;
import space.forloop.autotools.services.ScanService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobUnrarFiles implements UnaryOperator<ScheduledJob> {

    private static final long WAIT_DURATION = 15;

    private final ScanService scanService;

    private final HistoryService historyService;

    @Override
    public ScheduledJob apply(final ScheduledJob scheduledJob) {

        scanService
                .findFiles(scheduledJob.getSourceDirectory())
                .filter(f -> !historyService.isAlreadyProcessed(f.getId()))
                .filter(f -> f.getExtension().equalsIgnoreCase("rar"))
                .forEach(file -> extract(scheduledJob, file));

        log.info("Finished job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

        return scheduledJob;
    }

    private void extract(
            final ScheduledJob scheduledJob, final FileWrapper fileWrapper) {

        log.info("Extracting: {} -> {}", scheduledJob.getSourceDirectory(), scheduledJob.getTargetDirectory());

        try {
            List<ContentDescription> contentsDescription =
                    Junrar.getContentsDescription(new File(fileWrapper.getPath()));

            boolean noneMatch =
                    contentsDescription.stream()
                            .map(contentDescription -> contentDescription.path)
                            .noneMatch(path -> Files.exists(Path.of(scheduledJob.getTargetDirectory(), path)));

            if (noneMatch) {

                if (StringUtils.isEmpty(fileWrapper.getPath()) || StringUtils.isEmpty(scheduledJob.getTargetDirectory())) {
                    log.warn("Bad values into unrar, skipping...");
                    return;
                }

                waitForDirectory(fileWrapper.toNative().getParentFile());

                Junrar.extract(fileWrapper.getPath(), scheduledJob.getTargetDirectory());
            } else {
                log.info("{} already exists, not extracting.", fileWrapper.getPath());
            }

        } catch (IOException | RarException | InterruptedException e) {
            log.error("Unable to extract: {}", e.getMessage());
        } finally {
            historyService.saveFileWrapper(fileWrapper);
        }
    }

    /**
     * A bit hacky, but we want to make sure we don't unrar files that are in the process of being copied to the
     * target folder. For example if we're downloading a directory into /downloads - don't start copying the
     * directory to
     * /complete until the size of the directory stops growing.
     * <p>
     * We're assuming that if the filesize doesn't change in the time specified in
     * {@link JobUnrarFiles#WAIT_DURATION} that it must be done downloading.
     */
    protected void waitForDirectory(final File directory) throws IOException, InterruptedException {

        log.info("Waiting for directory: {}", directory.getAbsolutePath());

        BigInteger lastCheckedSize;

        do {
            lastCheckedSize = FileUtils.sizeOfDirectoryAsBigInteger(directory);

            TimeUnit.SECONDS.sleep(WAIT_DURATION);

        } while (!Objects.equals(FileUtils.sizeOfDirectoryAsBigInteger(directory), lastCheckedSize));

        log.info("Done waiting for directory: {}", directory.getAbsolutePath());
    }

}
