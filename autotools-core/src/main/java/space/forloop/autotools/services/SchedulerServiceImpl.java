/* Licensed under Apache-2.0 */
package space.forloop.autotools.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.jobs.JobFactory;
import space.forloop.autotools.jobs.RunningJobs;
import space.forloop.autotools.repositories.ScheduledJobRepository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

  private final JobFactory jobFactory;

  private final ScheduledExecutorService executor;

  private final ScheduledJobRepository repository;

  @Override
  public List<ScheduledJob> findAll() {

    return repository.findAll();
  }

  @Override
  public void delete(final ScheduledJob scheduledJob) {

    RunningJobs.stop(scheduledJob.getId());

    repository.delete(scheduledJob);
  }

  @Override
  public ScheduledJob saveAndStart(final ScheduledJob scheduledJob) {

    // Always stop the job before doing anything else here
    RunningJobs.stop(scheduledJob.getId());

    final ScheduledJob persistedJob = repository.save(scheduledJob);

    return scheduledJob.isEnabled() ? start(persistedJob) : scheduledJob;
  }

  @Override
  public ScheduledJob start(final ScheduledJob scheduledJob) {
    final Runnable runnable =
        () -> jobFactory.launch(scheduledJob.getJobType()).apply(scheduledJob);

    ScheduledFuture<?> scheduledFuture =
        executor.scheduleWithFixedDelay(
            runnable, 0, scheduledJob.getDelay(), TimeUnit.MILLISECONDS);
    RunningJobs.add(scheduledJob, scheduledFuture);

    log.info("Started job: {} ({})", scheduledJob.getName(), scheduledJob.getJobType());

    return scheduledJob;
  }

  @Override
  public List<ScheduledJob> startAll() {

    repository.findAllByEnabledIsTrue().forEach(this::start);

    return RunningJobs.findAll();
  }

  @PreDestroy
  public void cleanUp() throws InterruptedException {
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException interruptedException) {
      executor.shutdownNow();
      throw interruptedException;
    }
  }
}
