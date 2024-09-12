package graduatioin_project.course_graph.repository;

import graduatioin_project.course_graph.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Integer> {
    Optional<TrackEntity> findByTrackId(int trackId);
}
