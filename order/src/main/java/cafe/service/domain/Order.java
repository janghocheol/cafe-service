package cafe.service.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import cafe.service.OrderApplication;
import lombok.Data;

@Entity
@Table(name = "Order_table")
@Data

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;

    private Long customerId;

    private Long cafeId;

    private Long menuId;

    private Integer qty;

    private Long totalPrice;

    private String status;

    private Date orderDate;

    @PostPersist
    public void onPostPersist() {
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publish();

        cafe.service.external.Payment payment = new cafe.service.external.Payment();
        payment.setOrderId(getOrderId());
        payment.setCustomerId(getCustomerId());
        payment.setCafeId(getCafeId());
        payment.setMenuId(getMenuId());
        payment.setQty(getQty());
        payment.setTotalPrice(getTotalPrice());
        payment.setStatus("Paid");
        payment.setOrderDate(getOrderDate());

        OrderApplication.applicationContext.getBean(cafe.service.external.PaymentService.class)
                .startPayment(payment);
    }

    public void orderCancel() {
        setStatus("OrderCanceled");
        CustomerOrderCanceled customerOrderCanceled = new CustomerOrderCanceled(this);
        customerOrderCanceled.publishAfterCommit();
    }

    // //@PrePersist
    // public void onPrePersist() {
    //     // Get request from Payment
    //     cafe.service.external.Payment payment = OrderApplication.applicationContext
    //             .getBean(cafe.service.external.PaymentServiceImpl.class)
    //             .getPayment(Long.valueOf(getOrderId()));

    //     if (payment.getStatus() != "Paid")
    //         throw new RuntimeException("Out of Payment Service!  Try in a while ");
    // }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }

}
