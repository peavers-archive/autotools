/* Licensed under Apache-2.0 */
package space.forloop.autotools.services;

import java.nio.file.Path;
import space.forloop.autotools.domain.FileWrapper;

public interface ThumbnailService {

  Path create(FileWrapper fileWrapper);
}
