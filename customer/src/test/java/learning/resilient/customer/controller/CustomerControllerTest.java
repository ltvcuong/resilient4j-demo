package learning.resilient.customer.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

  @Test
  public void test_both_CBopen_bulkheadfull_CB_first() throws Exception {
    mockMvc.perform(get("/v1/customers/tech-error"));
    mockMvc.perform(get("/v1/customers/tech-error"));
    mockMvc.perform(get("/v1/customers/tech-error"));
    mockMvc.perform(get("/v1/customers/tech-error"));
    mockMvc.perform(get("/v1/customers/tech-error"));

    // CB now OPEN
    assertThat(
        circuitBreakerRegistry.circuitBreaker("order").getState(),
        CoreMatchers.is(CircuitBreaker.State.OPEN));

    // CB now HALF_OPEN -> allow 3 calls
    Thread.sleep(6000);
    assertThat(
        circuitBreakerRegistry.circuitBreaker("order").getState(),
        CoreMatchers.is(CircuitBreaker.State.HALF_OPEN));

    // use slow calls block exhaust bulkhead
    var bulkhead = 5;
    for (var i = 0; i < bulkhead; i++) {
      CompletableFuture.supplyAsync(
          () -> {
            try {
              return mockMvc.perform(get("/v1/customers/slow"));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
    }
    Thread.sleep(1000);

    // call slow again -> CB checks first
    mockMvc
        .perform(get("/v1/customers/slow"))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andExpect(
            MockMvcResultMatchers.jsonPath("code", CoreMatchers.is("CallNotPermittedException")));
  }

  @Test
  public void test_CBClosed_bulkheadfull() throws Exception {
    // use slow calls block exhaust bulkhead
    var bulkhead = 5;
    for (var i = 0; i < bulkhead; i++) {
      CompletableFuture.supplyAsync(
              () -> {
                try {
                  return mockMvc.perform(get("/v1/customers/slow"));
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });
    }
    Thread.sleep(1000);

    // call slow again -> CB checks first
    mockMvc
            .perform(get("/v1/customers/slow"))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError())
            .andExpect(
                    MockMvcResultMatchers.jsonPath("code", CoreMatchers.is("BulkheadFullException")));
  }
}
