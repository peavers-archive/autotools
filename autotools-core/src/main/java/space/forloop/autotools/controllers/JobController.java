/* Licensed under Apache-2.0 */
package space.forloop.autotools.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.forloop.autotools.domain.ScheduledJob;
import space.forloop.autotools.services.SchedulerService;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
public class JobController {

  private final SchedulerService schedulerService;

  @GetMapping
  public Flux<ScheduledJob> findAll() {

    return Flux.fromIterable(schedulerService.findAll());
  }

  @PostMapping
  public Mono<ScheduledJob> save(@RequestBody final ScheduledJob scheduledJob) {

    return Mono.just(schedulerService.saveAndStart(scheduledJob));
  }

  @DeleteMapping
  public Mono<Void> delete(@RequestBody final ScheduledJob scheduledJob) {
    schedulerService.delete(scheduledJob);

    return Mono.empty();
  }
}
