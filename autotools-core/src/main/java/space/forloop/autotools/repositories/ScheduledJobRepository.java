/* Licensed under Apache-2.0 */
package space.forloop.autotools.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.forloop.autotools.domain.ScheduledJob;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {

  List<ScheduledJob> findAllByEnabledIsTrue();
}
