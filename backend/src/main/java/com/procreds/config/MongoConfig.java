package com.procreds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * MongoDB configuration for ProCreds application
 * 
 * Configures:
 * - Database name
 * - Connection settings
 * - Auditing support (enabled via @EnableMongoAuditing in main class)
 * 
 * @author ProCreds Team
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "procreds";
    }
}

