/* Licensed under Apache-2.0 */
package space.forloop.autotools.domain;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DuplicateAdvance implements Comparable<DuplicateAdvance> {

  long hash;

  Path path;

  FileWrapper fileWrapper;

  @Override
  public int compareTo(final DuplicateAdvance o) {
    return -Long.compare(this.getFileWrapper().getSize(), o.getFileWrapper().getSize());
  }
}
