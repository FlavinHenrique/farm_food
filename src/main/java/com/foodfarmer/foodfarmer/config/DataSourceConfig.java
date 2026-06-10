package com.foodfarmer.foodfarmer.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:}")
    private String springUrl;

    @Value("${spring.datasource.username:}")
    private String springUsername;

    @Value("${spring.datasource.password:}")
    private String springPassword;

    @Bean
    DataSource dataSource() {
        String rawUrl = firstNonBlank(
            System.getenv("SPRING_DATASOURCE_URL"),
            System.getenv("JDBC_DATABASE_URL"),
            System.getenv("DATABASE_URL"),
            springUrl
        );

        String jdbcUrl = toJdbcUrl(rawUrl);
        String username = firstNonBlank(
            System.getenv("SPRING_DATASOURCE_USERNAME"),
            System.getenv("DATABASE_USERNAME"),
            springUsername,
            extractUsername(rawUrl)
        );
        String password = firstNonBlank(
            System.getenv("SPRING_DATASOURCE_PASSWORD"),
            System.getenv("DATABASE_PASSWORD"),
            springPassword,
            extractPassword(rawUrl)
        );

        if (!StringUtils.hasText(jdbcUrl)) {
            // Se não houver URL configurada, a aplicação deve falhar ao tentar usar o PostgreSQL
            throw new IllegalStateException("Nenhuma URL de banco de dados configurada em variáveis de ambiente ou application.properties");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(getInt("DB_POOL_MAX_SIZE", 5));
        config.setMinimumIdle(getInt("DB_POOL_MIN_IDLE", 1));
        config.setConnectionTimeout(getLong("DB_CONNECTION_TIMEOUT_MS", 30000L));

        return new HikariDataSource(config);
    }

    private static String toJdbcUrl(String rawUrl) {
        if (!StringUtils.hasText(rawUrl)) {
            return null;
        }

        if (rawUrl.startsWith("jdbc:postgresql://")) {
            return rawUrl;
        }

        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(rawUrl);
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                StringBuilder jdbc = new StringBuilder("jdbc:postgresql://")
                    .append(uri.getHost())
                    .append(":")
                    .append(port)
                    .append(uri.getPath());

                if (StringUtils.hasText(uri.getRawQuery())) {
                    jdbc.append("?").append(uri.getRawQuery());
                }

                return jdbc.toString();
            } catch (URISyntaxException ex) {
                throw new IllegalStateException("Invalid database URL format.", ex);
            }
        }

        return rawUrl;
    }

    private static String extractUsername(String rawUrl) {
        return extractUserInfoPart(rawUrl, 0);
    }

    private static String extractPassword(String rawUrl) {
        return extractUserInfoPart(rawUrl, 1);
    }

    private static String extractUserInfoPart(String rawUrl, int index) {
        if (!StringUtils.hasText(rawUrl) || rawUrl.startsWith("jdbc:")) {
            return null;
        }

        try {
            URI uri = new URI(rawUrl);
            String userInfo = uri.getUserInfo();
            if (!StringUtils.hasText(userInfo)) {
                return null;
            }

            String[] parts = userInfo.split(":", 2);
            return parts.length > index ? parts[index] : null;
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static int getInt(String envName, int defaultValue) {
        String rawValue = System.getenv(envName);
        if (!StringUtils.hasText(rawValue)) {
            return defaultValue;
        }
        return Integer.parseInt(rawValue);
    }

    private static long getLong(String envName, long defaultValue) {
        String rawValue = System.getenv(envName);
        if (!StringUtils.hasText(rawValue)) {
            return defaultValue;
        }
        return Long.parseLong(rawValue);
    }
}
