package space.forloop.autotools.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import space.forloop.autotools.domain.History;

public interface HistoryRepository extends JpaRepository<History, Long> {

    boolean existsHistoryByFileWrapperId(String id);

}
