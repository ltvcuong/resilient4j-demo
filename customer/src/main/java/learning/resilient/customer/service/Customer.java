package learning.resilient.customer.service;

import learning.resilient.customer.extservice.Order;

import java.util.List;

public class Customer {
    private final String customerId;
    private final String name;
    private final List<Order> orders;

    public Customer(String customerId, String name, List<Order> orders) {
        this.customerId = customerId;
        this.name = name;
        this.orders = orders;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
