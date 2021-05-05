/* Licensed under Apache-2.0 */
package space.forloop.autotools.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import space.forloop.autotools.domain.Directory;
import space.forloop.autotools.domain.FileWrapper;

@Slf4j
@UtilityClass
public class DeleteUtils {

  public Publisher<FileWrapper> deleteFile(final FileWrapper fileWrapper) {
    delete("Deleting file {} ", fileWrapper.getPath());

    return Mono.just(fileWrapper);
  }

  public Publisher<Directory> deleteDirectory(final Directory directory) {
    delete("Deleting directory {} ", directory.getPath());

    return Mono.just(directory);
  }

  private boolean isReadable(final String path) {
    return Files.isReadable(Path.of(path));
  }

  private void delete(final String message, final String path) {

    log.info(message, path);

    try {
      if (isReadable(path)) {
        Files.deleteIfExists(Path.of(path));
      }
    } catch (final IOException e) {
      log.warn("Unable to delete {}", path);
    }
  }
}
