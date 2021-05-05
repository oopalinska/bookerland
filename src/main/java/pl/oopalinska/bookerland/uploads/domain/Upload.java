package pl.oopalinska.bookerland.uploads.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Upload {
    String id;
    byte[] file;
    String contentType;
    String fileName;
    LocalDateTime createdAt;
}
