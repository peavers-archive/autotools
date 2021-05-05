package space.forloop.autotools.services;

import space.forloop.autotools.domain.FileWrapper;
import space.forloop.autotools.domain.History;

import java.util.List;

public interface HistoryService {

    History saveFileWrapper(FileWrapper fileWrapper);

    boolean isAlreadyProcessed(String fileId);

    List<History> findAll();

    void deleteById(long id);

    void deleteAll();
}
