package graduatioin_project.course_graph.repository;

import graduatioin_project.course_graph.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByuId(String uId);
    Optional<UserEntity> findByuPwd(String uPwd);
}
