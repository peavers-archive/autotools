/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.ScanService;

@ExtendWith(MockitoExtension.class)
class JobDeleteFilesByNameTest {

  @Mock private ScanService scanService;

  private JobDeleteFilesByName job;

  @BeforeEach
  void setUp() {
    job = new JobDeleteFilesByName(scanService);
  }

  @Test
  void shouldDeleteFile() {
    final ScheduledJob scheduledJob = ScheduledJob.builder().deleteByName("sample").build();
    final FileWrapper fileWrapper = FileWrapper.builder().name("should_delete_sample.mp4").build();

    final boolean actual = job.toDelete(scheduledJob, fileWrapper);

    assertTrue(actual, "Should delete file");
  }

  @Test
  void shouldNotDeleteFile() {
    final ScheduledJob scheduledJob = ScheduledJob.builder().deleteByName("sample").build();
    final FileWrapper fileWrapper = FileWrapper.builder().name("should_not_delete.mp4").build();

    final boolean actual = job.toDelete(scheduledJob, fileWrapper);

    assertFalse(actual, "Should not delete file");
  }
}
