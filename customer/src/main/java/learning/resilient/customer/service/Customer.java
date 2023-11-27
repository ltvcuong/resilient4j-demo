package learning.resilient.customer.service;

import learning.resilient.customer.extservice.Order;

import java.util.List;

public record Customer(String customerId, String name, List<Order> orders) {}
