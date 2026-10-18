package org.vidyaai.videoservices.exception;

import lombok.Getter;

@Getter
public class FileTooLargeException extends VideoServiceException {

    private final long maxSizeBytes;
    private final long actualSizeBytes;

    public FileTooLargeException(long maxSizeBytes, long actualSizeBytes) {
        super(String.format("File size %d bytes exceeds limit of %d bytes",
                actualSizeBytes, maxSizeBytes), "VID_003");
        this.maxSizeBytes = maxSizeBytes;
        this.actualSizeBytes = actualSizeBytes;
    }
}