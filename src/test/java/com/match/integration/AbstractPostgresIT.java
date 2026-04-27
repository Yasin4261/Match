package com.match.integration;

import io.minio.MinioClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base for integration tests:
 *  - Real PostGIS Postgres via a SINGLETON Testcontainer shared by every {@code *IT} class
 *    (Flyway migrations applied automatically on first context boot).
 *  - Redis and MinIO are mocked so the whole context boots without those services.
 *  - A no-op JavaMailSenderImpl is supplied so MailHealthContributor finds at least one bean.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@Import(AbstractPostgresIT.NoopMailConfig.class)
public abstract class AbstractPostgresIT {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("match_test")
            .withUsername("match")
            .withPassword("match")
            .withReuse(false);
        POSTGRES.start();
        // JVM-wide; container will be stopped when the JVM exits.
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
        r.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        r.add("spring.flyway.user", POSTGRES::getUsername);
        r.add("spring.flyway.password", POSTGRES::getPassword);
    }

    // External services mocked to avoid network during context boot
    @MockBean StringRedisTemplate redisTemplate;
    @MockBean MinioClient minioClient;

    @TestConfiguration
    static class NoopMailConfig {
        @Bean
        JavaMailSenderImpl javaMailSender() { return new JavaMailSenderImpl(); }
    }
}





