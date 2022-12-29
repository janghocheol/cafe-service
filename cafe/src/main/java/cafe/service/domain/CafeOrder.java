package cafe.service.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.Table;

import cafe.service.CafeApplication;
import lombok.Data;

@Entity
@Table(name = "CafeOrder_table")
@Data

public class CafeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long cafeOrderId;

    private Long customerId;

    private Long cafeId;

    private Long menuId;

    private Integer qty;

    private Date orderDate;

    private String status;

    private Long orderId;

    @PostPersist
    public void onPostPersist() {
    }

    public static CafeOrderRepository repository() {
        CafeOrderRepository cafeOrderRepository = CafeApplication.applicationContext.getBean(CafeOrderRepository.class);
        return cafeOrderRepository;
    }

    public void makeStart() {
        setStatus("MakeStarted");
        MakeStarted makeStarted = new MakeStarted(this);
        makeStarted.publishAfterCommit();
    }

    public void makeComplete() {
        setStatus("MakeCompleted");
        MakeCompleted makeCompleted = new MakeCompleted(this);
        makeCompleted.publishAfterCommit();
    }

    public void orderApprove() {
        setStatus("OrderApproved");
        OrderApproved orderApproved = new OrderApproved(this);
        orderApproved.publishAfterCommit();
    }

    public void orderCancel() {
        setStatus("OrderCanceled");
        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();
    }

    public static void addToCafeOrder(Paid paid) {
        CafeOrder cafeOrder = new CafeOrder();
        cafeOrder.setCustomerId(paid.getCustomerId());
        cafeOrder.setCafeId(paid.getCafeId());
        cafeOrder.setMenuId(paid.getMenuId());
        cafeOrder.setQty(paid.getQty());
        cafeOrder.setOrderDate(paid.getOrderDate());
        cafeOrder.setStatus("Paid");
        cafeOrder.setOrderId(paid.getOrderId());
        cafeOrder.repository().save(cafeOrder);
    }

    public static void changeCafeOrder(PaymentCanceled paymentCanceled) {
        repository().findById(paymentCanceled.getOrderId()).ifPresent(cafeOrder -> {
            cafeOrder.setStatus("paymentCanceled"); // do something
            repository().save(cafeOrder);
        });
    }

}
