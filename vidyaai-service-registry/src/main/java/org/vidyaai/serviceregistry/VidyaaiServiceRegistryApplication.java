package org.vidyaai.serviceregistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableEurekaServer
@Slf4j
public class VidyaaiServiceRegistryApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context =
                    SpringApplication.run(VidyaaiServiceRegistryApplication.class, args);
            Environment env = context.getEnvironment();
            logApplicationStartup(env);
        } catch (Exception e) {
            log.error("Failed to start VidyaAI Service Registry: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        String serverPort = env.getProperty("server.port", "8761");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not determine host address, defaulting to localhost");
        }

        log.info("""

                ----------------------------------------------------------
                  VidyaAI Service Registry (Eureka Server) is UP!
                ----------------------------------------------------------
                  Local:      {}://localhost:{}{}
                  External:   {}://{}:{}{}
                  Profile(s): {}
                  Eureka UI:  {}://localhost:{}{}/eureka
                ----------------------------------------------------------
                """,
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                String.join(", ", env.getActiveProfiles().length == 0
                        ? env.getDefaultProfiles() : env.getActiveProfiles()),
                protocol, serverPort, contextPath);
    }
}