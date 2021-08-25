package com.intergamma.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@EnableCaching
@SpringBootApplication
public class InventoryApplication {

    private static final Logger log = LoggerFactory.getLogger(InventoryApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(InventoryApplication.class);
        ConfigurableEnvironment runningEnvironment = application.run(args).getEnvironment();

        loggingApplicationStartup(runningEnvironment);
    }

    private static void loggingApplicationStartup(final ConfigurableEnvironment runningEnvironment) {
        String protocol = Optional.ofNullable(runningEnvironment.getProperty("server.ssl.key-store")).map(s -> "https").orElse("http");
        String contextPath = Optional.ofNullable(runningEnvironment.getProperty("springdoc.swagger-ui.path")).orElse("/");
        String serverPort = Optional.ofNullable(runningEnvironment.getProperty("server.port")).orElse("7070");

        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info("\n" +
                                        "______________     ____________     ____________     ______________     ____________     ____________\n" +
                                        "    Application: '{}' is running! Access URLs:\n" +
                                        "    Local:{}://localhost:{}{}\n" +
                                        "    External:{}://{}:{}{}\n" +
                                        "    Profile(s):{}\n" +
                                        "______________     ____________     ____________     ______________     ____________     ____________"
                        ,
                        runningEnvironment.getProperty("spring.application.name"),
                        protocol,
                        serverPort,
                        contextPath,
                        protocol,
                        hostAddress,
                        serverPort,
                        contextPath,
                        runningEnvironment.getActiveProfiles());
    }

}
