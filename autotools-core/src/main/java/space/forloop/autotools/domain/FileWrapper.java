/* Licensed under Apache-2.0 */
package space.forloop.autotools.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FileWrapper {

  String id;

  String name;

  String path;

  String extension;

  long size;

  LocalDateTime createdTime;

  LocalDateTime lastAccessTime;

  LocalDateTime lastModifiedTime;

  LocalDateTime lastWatched;

  public java.io.File toNative() {
    return new java.io.File(this.path);
  }
}
