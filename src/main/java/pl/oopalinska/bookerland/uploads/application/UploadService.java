package pl.oopalinska.bookerland.uploads.application;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.uploads.application.ports.UploadUseCase;
import pl.oopalinska.bookerland.uploads.db.UploadJpaRepository;
import pl.oopalinska.bookerland.uploads.domain.Upload;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UploadService implements UploadUseCase {
    private final UploadJpaRepository repository;

    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = new Upload(
                command.getFileName(),
                command.getContentType(),
                command.getFile()
        );
        repository.save(upload);
        System.out.println("Upload saved: " + upload.getFileName() + " with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
