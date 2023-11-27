package learning.resilient.customer.extservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Order {
  private String orderId;
  private String customerId;
  private double totalPrice;
//  private LocalDateTime orderDate;
}
