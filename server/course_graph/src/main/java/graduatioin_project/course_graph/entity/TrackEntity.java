package graduatioin_project.course_graph.entity;

import graduatioin_project.course_graph.dto.TrackDTO;
import lombok.Builder;
import lombok.Getter;
import jakarta.persistence.*;

@Entity
@Getter
@Table(name = "track")
public class TrackEntity {
    @Id
    @Column(name = "track_id")
    private int trackId;

    @Column(name = "track_name")
    private String trackName;

    @Builder
    public static TrackEntity toTrackEntity(TrackDTO trackDTO) {
        TrackEntity trackEntity = new TrackEntity();

        trackEntity.trackId = trackDTO.getTrackId();
        trackEntity.trackName = trackDTO.getTrackName();
        return trackEntity;
    }
}
