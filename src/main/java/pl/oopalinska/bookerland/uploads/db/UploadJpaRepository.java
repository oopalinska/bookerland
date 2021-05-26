package pl.oopalinska.bookerland.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.oopalinska.bookerland.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {

}
