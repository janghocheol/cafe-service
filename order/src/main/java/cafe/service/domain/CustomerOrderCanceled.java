package cafe.service.domain;

import cafe.service.domain.*;
import cafe.service.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class CustomerOrderCanceled extends AbstractEvent {

    private Long orderId;
    private Long customerId;
    private Long cafeId;
    private Long menuId;
    private Integer qty;
    private Long totalPrice;
    private String status;
    private Date orderDate;

    public CustomerOrderCanceled(Order aggregate){
        super(aggregate);
    }
    public CustomerOrderCanceled(){
        super();
    }
}
