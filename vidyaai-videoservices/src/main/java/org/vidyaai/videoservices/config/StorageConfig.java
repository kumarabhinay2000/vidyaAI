package org.vidyaai.videoservices.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "storage")
@Data
@Slf4j
public class StorageConfig {

    private Minio minio = new Minio();
    private Local local = new Local();

    @Data
    public static class Minio {
        private boolean enabled = true;
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String region = "us-east-1";
        private int presignedUrlExpirySeconds = 3600;
    }

    @Data
    public static class Local {
        private String basePath = "./storage/videos";
        private String publicBaseUrl = "http://localhost:8082/storage";
    }

    @Bean
    public MinioClient minioClient() {
        log.info("Configuring MinIO client for endpoint={}, bucket={}",
                minio.getEndpoint(), minio.getBucket());
        return MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
    }
}