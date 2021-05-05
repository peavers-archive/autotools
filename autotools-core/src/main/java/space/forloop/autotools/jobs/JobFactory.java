/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.JobType;
import space.forloop.autotools.domain.ScheduledJob;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobFactory {

  private final JobCopyMediaFiles copyMediaFiles;

  private final JobDeleteEmptyDirectories deleteEmptyDirectories;

  private final JobDeleteFilesBySize deleteFilesBySize;

  private final JobDeleteFilesByName deleteFilesByName;

  private final JobDuplicateMediaAdvance duplicateMediaAdvance;

  private final JobDuplicateMediaBasic duplicateMediaBasic;

  private final JobUnrarFiles unrarFiles;

  public UnaryOperator<ScheduledJob> launch(JobType jobType) {

    return switch (jobType) {
      case COPY_MEDIA_FILES -> copyMediaFiles;
      case DELETE_EMPTY_DIRECTORIES -> deleteEmptyDirectories;
      case DELETE_FILES_BY_SIZE -> deleteFilesBySize;
      case DELETE_FILES_BY_NAME -> deleteFilesByName;
      case DUPLICATE_MEDIA_ADVANCE -> duplicateMediaAdvance;
      case DUPLICATE_MEDIA_BASIC -> duplicateMediaBasic;
      case UNRAR_FILES -> unrarFiles;
    };
  }
}
