package pl.oopalinska.bookerland.uploads.application.ports;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.oopalinska.bookerland.uploads.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);
    Optional<Upload> getById(String id);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String fileName;
        byte[] file;
        String contentType;
    }
}
