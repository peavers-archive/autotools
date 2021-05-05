/* Licensed under Apache-2.0 */
package space.forloop.autotools.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import space.forloop.autotools.converters.DirectoryConverter;
import space.forloop.autotools.converters.FileConverter;
import space.forloop.autotools.domain.Directory;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.utils.PathUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

  private final FileConverter fileConverter;

  private final DirectoryConverter directoryConverter;

  @Override
  public Stream<FileWrapper> findFiles(final String path) {
    return getFileStream(path);
  }

  @Override
  public Flux<FileWrapper> findFilesFlux(final String path) {
    return Flux.fromStream(getFileStream(path));
  }

  @Override
  public Flux<Directory> findDirectoriesFlux(final String path) {
    return Flux.fromStream(getDirectoryStream(path));
  }

  private Stream<FileWrapper> getFileStream(final String path) {
    try {
      return Files.walk(Paths.get(path))
          .filter(PathUtils::isReadable)
          .filter(PathUtils::isNotDirectory)
          .map(fileConverter::convert);
    } catch (final IOException e) {
      return Stream.empty();
    }
  }

  private Stream<Directory> getDirectoryStream(final String path) {
    try {
      return Files.walk(Paths.get(path))
          .filter(PathUtils::isReadable)
          .filter(PathUtils::isDirectory)
          .map(directoryConverter::convert);
    } catch (final IOException e) {
      return Stream.empty();
    }
  }
}
