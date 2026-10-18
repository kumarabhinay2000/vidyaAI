package org.vidyaai.videoservices.producer;

import org.vidyaai.videoservices.entity.Video;

public interface VideoEventProducer {

    void publishVideoUploaded(Video video);

    void publishVideoUpdated(Video video, String updatedField);

    void publishVideoDeleted(Video video);
}