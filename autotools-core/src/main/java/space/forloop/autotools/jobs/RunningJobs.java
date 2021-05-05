/* Licensed under Apache-2.0 */
package space.forloop.autotools.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import space.forloop.autotools.domain.ScheduledJob;

@Slf4j
@UtilityClass
public class RunningJobs {

  private final ConcurrentHashMap<Long, ScheduledFuture<?>> JOBS = new ConcurrentHashMap<>();

  public void add(final ScheduledJob job, final ScheduledFuture<?> schedule) {

    JOBS.put(job.getId(), schedule);
  }

  public void stop(Long id) {

    if (JOBS.containsKey(id)) {
      JOBS.get(id).cancel(true);
      JOBS.remove(id);
    }
  }

  public List<ScheduledJob> findAll() {

    return JOBS.entrySet().stream()
        .filter(ScheduledJob.class::isInstance)
        .map(ScheduledJob.class::cast)
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
