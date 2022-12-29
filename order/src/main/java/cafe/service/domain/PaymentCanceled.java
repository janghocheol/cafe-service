package cafe.service.domain;

import cafe.service.infra.AbstractEvent;
import lombok.Data;
import java.util.*;


@Data
public class PaymentCanceled extends AbstractEvent {

    private Long paymentId;
    private Long customerId;
    private Long cafeId;
    private Long menuId;
    private Integer qty;
    private Long totalPrice;
    private Date orderDate;
    private Long orderId;
}
