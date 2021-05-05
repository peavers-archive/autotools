/* Licensed under Apache-2.0 */
package space.forloop.autotools.services;

import java.util.stream.Stream;
import reactor.core.publisher.Flux;
import space.forloop.autotools.domain.Directory;
import space.forloop.autotools.domain.FileWrapper;

public interface ScanService {

  Stream<FileWrapper> findFiles(String path);

  Flux<FileWrapper> findFilesFlux(String path);

  Flux<Directory> findDirectoriesFlux(String path);
}
