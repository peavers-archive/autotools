/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.ScanService;

@ExtendWith(MockitoExtension.class)
class JobDeleteFilesBySizeTest {

  @Mock private ScanService scanService;

  private JobDeleteFilesBySize job;

  @BeforeEach
  void setUp() {
    job = new JobDeleteFilesBySize(scanService);
  }

  @Test
  void shouldDeleteFile(@TempDir Path tempDir) throws IOException {
    final java.io.File testDirectory = tempDir.toFile();
    final java.io.File file = Path.of(testDirectory.toString(), "test.txt").toFile();

    FileUtils.writeLines(file, Collections.singleton("hello world!")); // ~13 bytes

    final ScheduledJob scheduledJob = ScheduledJob.builder().lessThanThreshold(15).build();
    final FileWrapper jobFileWrapper = FileWrapper.builder().size(Files.size(file.toPath())).build();

    final boolean actual = job.toDelete(scheduledJob, jobFileWrapper);

    assertTrue(actual, "Should delete file");
  }

  @Test
  void shouldNotDeleteFile(@TempDir Path tempDir) throws IOException {
    final java.io.File testDirectory = tempDir.toFile();
    final java.io.File file = Path.of(testDirectory.toString(), "test.txt").toFile();

    FileUtils.writeLines(file, Collections.singleton("hello world!")); // ~13 bytes

    final ScheduledJob scheduledJob = ScheduledJob.builder().lessThanThreshold(12).build();
    final FileWrapper jobFileWrapper = FileWrapper.builder().size(Files.size(file.toPath())).build();

    final boolean actual = job.toDelete(scheduledJob, jobFileWrapper);

    assertFalse(actual, "Should not delete file");
  }
}
