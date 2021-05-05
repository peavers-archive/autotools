/* Licensed under Apache-2.0 */
package space.forloop.autotools.services;

import java.util.List;
import space.forloop.autotools.domain.ScheduledJob;

public interface SchedulerService {

  List<ScheduledJob> startAll();

  ScheduledJob start(ScheduledJob scheduledJob);

  ScheduledJob saveAndStart(ScheduledJob scheduledJob);

  List<ScheduledJob> findAll();

  void delete(ScheduledJob scheduledJob);
}
