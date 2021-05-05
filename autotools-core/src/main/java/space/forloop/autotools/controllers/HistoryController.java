/* Licensed under Apache-2.0 */
package space.forloop.autotools.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.forloop.autotools.domain.History;
import space.forloop.autotools.services.HistoryService;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public Flux<History> findAll() {

        return Flux.fromIterable(historyService.findAll());
    }

    @GetMapping("/clear")
    public Mono<Void> clear() {
        historyService.deleteAll();

        return Mono.empty();
    }
}
