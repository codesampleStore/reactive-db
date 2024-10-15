package com.example.reactivedb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

class CustomerControllerTest {

  @Autowired
  private WebTestClient client;

  @Autowired
  private DatabaseClient databaseClient;

  @BeforeEach
  void setUp() {
    var statements = List.of(
        """
        DROP TABLE IF EXISTS customer;
        CREATE TABLE customer(
            id long generated always as identity primary key,
            first_name VARCHAR(100) NOT NULL,
            last_name VARCHAR(100) NOT NULL
        );
        INSERT INTO customer (first_name, last_name) VALUES ('Malcolm', 'Reynolds');
        INSERT INTO customer (first_name, last_name) VALUES ('Zoë', 'Washburne');
        INSERT INTO customer (first_name, last_name) VALUES ('Hoban', 'Washburne');
        INSERT INTO customer (first_name, last_name) VALUES ('Jayne', 'Cobb');
        INSERT INTO customer (first_name, last_name) VALUES ('Kaylee', 'Frye');
        """
    );
    statements.forEach(it -> databaseClient.sql(it)
        .fetch()
        .rowsUpdated()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete());
  }

  private List<Long> getIds() {
    return databaseClient.sql("select id from customer")
        .map(row -> row.get("id", Long.class))
        .all()
        .collectList()
        .block();
  }

  @Test
  void findAll() {
    client.get()
        .uri("/customers")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Customer.class)
        .hasSize(5);
  }
}