package br.com.ambev.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://admin:admin@localhost:27017/orderdb?authSource=admin");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> builder
                        .maxSize(50)
                        .minSize(10)
                        .maxWaitTime(120, TimeUnit.SECONDS)
                )
                .writeConcern(WriteConcern.ACKNOWLEDGED.withJournal(false))
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}
