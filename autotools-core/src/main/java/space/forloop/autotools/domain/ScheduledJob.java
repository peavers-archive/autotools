/* Licensed under Apache-2.0 */
package space.forloop.autotools.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledJob {

  @Id @GeneratedValue private long id;

  private String name;

  private JobType jobType;

  private long delay;

  private boolean enabled;

  private String sourceDirectory;

  private String targetDirectory;

  private long lessThanThreshold;

  private long greaterThanThreshold;

  private String deleteByName;

  private String ignoreWords;
}
