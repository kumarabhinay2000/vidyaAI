package org.vidyaai.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class VidyaaiApiGatewayApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context =
                    SpringApplication.run(VidyaaiApiGatewayApplication.class, args);
            logStartupInfo(context.getEnvironment());
        } catch (Exception e) {
            log.error("Failed to start VidyaAI API Gateway: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static void logStartupInfo(Environment env) {
        String protocol = "http";
        String port = env.getProperty("server.port", "8080");
        String host = "localhost";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not resolve host");
        }
        log.info("""

                ----------------------------------------------------------
                  VidyaAI API Gateway is UP!
                ----------------------------------------------------------
                  Local:      {}://localhost:{}
                  External:   {}://{}:{}
                  Profile(s): {}
                  Eureka:     {}
                ----------------------------------------------------------
                """,
                protocol, port, protocol, host, port,
                String.join(", ", env.getActiveProfiles().length == 0
                        ? env.getDefaultProfiles() : env.getActiveProfiles()),
                env.getProperty("eureka.client.service-url.defaultZone"));
    }
}