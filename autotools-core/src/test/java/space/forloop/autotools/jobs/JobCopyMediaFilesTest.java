/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.HistoryService;
import space.forloop.autotools.services.ScanService;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JobCopyMediaFilesTest {

  @Mock private ScanService scanService;

  @Mock private HistoryService historyService;

  private JobCopyMediaFiles job;

  @BeforeEach
  void setUp() {
    job = new JobCopyMediaFiles(scanService, historyService);
  }

  @Test
  void shouldCopyFile() {
    final ScheduledJob scheduledJob =
        ScheduledJob.builder().ignoreWords("sample, test, something,").build();
    final FileWrapper fileWrapper = FileWrapper.builder().path("this/is/a/passing/file/java.mp4").build();

    Predicate<FileWrapper> filePredicate = job.checkIgnored(scheduledJob);

    assertTrue(filePredicate.test(fileWrapper), "Should copy file");
  }

  @Test
  void shouldNotCopyFile() {
    final ScheduledJob scheduledJob =
        ScheduledJob.builder().ignoreWords("sample, test, something,").build();
    final FileWrapper fileWrapper = FileWrapper.builder().path("this/is/a/sample/test/java.mp4").build();

    Predicate<FileWrapper> filePredicate = job.checkIgnored(scheduledJob);

    assertFalse(filePredicate.test(fileWrapper), "Should not copy file");
  }
}
