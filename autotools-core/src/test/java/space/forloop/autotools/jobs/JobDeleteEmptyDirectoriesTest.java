/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.forloop.autotools.domain.Directory;
import space.forloop.autotools.services.ScanService;

@ExtendWith(MockitoExtension.class)
class JobDeleteEmptyDirectoriesTest {

  @Mock private ScanService scanService;

  private JobDeleteEmptyDirectories job;

  @BeforeEach
  void setUp() {
    job = new JobDeleteEmptyDirectories(scanService);
  }

  @Test
  void shouldDeleteDirectories(@TempDir Path tempDir) {
    final File testDirectory = tempDir.toFile();

    final Directory directory =
        Directory.builder()
            .children(Objects.requireNonNull(testDirectory.listFiles()).length)
            .build();

    final boolean actual = job.toDelete(directory);

    assertTrue(actual, "Should delete directory");
  }

  @Test
  void shouldNotDeleteDirectories(@TempDir Path tempDir) throws IOException {
    final File testDirectory = tempDir.toFile();
    final boolean ignored = Path.of(testDirectory.toString(), "test.txt").toFile().createNewFile();

    final Directory directory =
        Directory.builder()
            .children(Objects.requireNonNull(testDirectory.listFiles()).length)
            .build();

    final boolean actual = job.toDelete(directory);

    assertFalse(actual, "Should not delete directory");
  }
}
