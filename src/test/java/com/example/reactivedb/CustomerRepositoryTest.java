package com.example.reactivedb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class CustomerRepositoryTest {

  @Autowired
  private CustomerRepository customerRepository;

  private List<Customer> customers = List.of(
      new Customer(null, "Malcolm", "Reynolds"),
      new Customer(null, "Zoë", "Washburne"),
      new Customer(null, "Hoban", "Washburne"),
      new Customer(null, "Jayne", "Cobb"),
      new Customer(null, "Kaylee", "Frye"));


  @BeforeEach
  void setUp() {
    customerRepository.saveAll(customers)
        .then()
        .block();
    customers = customerRepository.findAll()
            .collectList().block();
    System.out.println(customers);
  }

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll()
        .then()
        .block();
  }

  @Test
  void fetchAllCustomers() {
    customerRepository.findAll()
        .doOnNext(System.out::println)
        .as(StepVerifier::create)
        .expectNextCount(5)
        .verifyComplete();
  }

  // Check the other query methods by fetching the first customer by id, then searching by last name.
  @Test
  void fetchCustomerById() {
    customerRepository.findById(customers.get(0).id())
        .doOnNext(System.out::println)
        .as(StepVerifier::create)
        .expectNextMatches(customer -> customer.firstName().equals("Malcolm"))
        .verifyComplete();
  }
}