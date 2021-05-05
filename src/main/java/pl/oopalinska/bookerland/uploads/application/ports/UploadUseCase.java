package pl.oopalinska.bookerland.uploads.application.ports;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.oopalinska.bookerland.uploads.domain.Upload;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String fileName;
        byte[] file;
        String contentType;
    }
}
