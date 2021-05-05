package pl.oopalinska.bookerland.uploads.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.uploads.application.ports.UploadUseCase;
import pl.oopalinska.bookerland.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService implements UploadUseCase {
    private final Map<String, Upload> storage = new ConcurrentHashMap<>();

    @Override
    public Upload save(SaveUploadCommand command) {
        String newId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        Upload upload = new Upload(
                newId,
                command.getFile(),
                command.getContentType(),
                command.getFileName(),
                LocalDateTime.now()
        );
        storage.put(upload.getId(), upload);
        System.out.println("Upload saved: " + upload.getFileName() + " with id: " + newId);
        return upload;
    }

    @Override
    public Optional<Upload> getById(String id) {
        storage.get(id);
        return Optional.ofNullable(storage.get(id));
    }
}
