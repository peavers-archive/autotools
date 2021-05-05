package space.forloop.autotools.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.History;
import space.forloop.autotools.repositories.HistoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository repository;

    @Override
    public History saveFileWrapper(final FileWrapper fileWrapper) {

        return repository.save(History
                .builder()
                .path(fileWrapper.getPath())
                .fileWrapperId(fileWrapper.getId())
                .build());
    }

    @Override
    public boolean isAlreadyProcessed(final String fileId) {

        return repository.existsHistoryByFileWrapperId(fileId);
    }

    @Override
    public List<History> findAll() {

        return repository.findAll();
    }

    @Override
    public void deleteById(final long id) {

        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {

        repository.deleteAll();
    }
}
