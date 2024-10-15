package com.example.reactivedb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerRepository repository;

  @Autowired
  public CustomerController(CustomerRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public Flux<Customer> findAll() {
    return repository.findAll();
  }

  @GetMapping("{id}")
  public Mono<Customer> findById(@PathVariable Long id) {
    return repository.findById(id).switchIfEmpty(
        Mono.error(new IllegalArgumentException(
            "Customer with id %d not found".formatted(id))));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Customer> create(@RequestBody Customer customer) {
    return repository.save(customer);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable Long id) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException(
            "Customer with id %d not found".formatted(id))))
        .flatMap(repository::delete);
  }


}
