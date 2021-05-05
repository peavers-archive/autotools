/* Licensed under Apache-2.0 */
package space.forloop.autotools.startup; /* Licensed under Apache-2.0 */

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import space.forloop.autotools.domain.History;
import space.forloop.autotools.services.HistoryService;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanHistoryTask implements ApplicationListener<ApplicationReadyEvent> {

    private final HistoryService historyService;

    @Override
    public void onApplicationEvent(final @NonNull ApplicationReadyEvent event) {

        historyService.findAll().stream()
                .filter(history -> StringUtils.isNotEmpty(history.getPath()))
                .filter(history -> Files.notExists(Path.of(history.getPath())))
                .mapToLong(History::getId)
                .forEach(historyService::deleteById);
    }

}
