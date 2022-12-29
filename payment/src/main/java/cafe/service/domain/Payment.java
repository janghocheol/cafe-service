package cafe.service.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import cafe.service.PaymentApplication;
import cafe.service.external.PGDemo;
import lombok.Data;

@Entity
@Table(name = "Payment_table")
@Data

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long paymentId;

    private Long customerId;

    private Long cafeId;

    private Long menuId;

    private Integer qty;

    private Long totalPrice;

    private Date orderDate;

    private Long approvalCode;

    private String status;

    private Long orderId;

    @PostPersist
    public void onPostPersist() {
        Paid paid = new Paid(this);
        paid.publishAfterCommit();

    }

    @PrePersist
    public void onPrePersist() {
        Long approvalCode = pg().startTransaction();
        if(approvalCode!=0){
            setApprovalCode(approvalCode);
        }
    }

    @PreUpdate
    public void onPreUpdate() {

        PaymentCanceled paymentCanceled = new PaymentCanceled(this);
        paymentCanceled.publishAfterCommit();

    }

    @PostLoad
    public void makeDelay() {
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static PaymentRepository repository() {
        PaymentRepository paymentRepository = PaymentApplication.applicationContext.getBean(PaymentRepository.class);
        return paymentRepository;
    }

    public static PGDemo pg() {
        PGDemo pg = PaymentApplication.applicationContext.getBean(PGDemo.class);
        return pg;
    }

    public static void cancelPayment(CustomerOrderCanceled customerOrderCanceled) {
        repository().findByOrderId(customerOrderCanceled.getOrderId()).ifPresent(payment -> {
            payment.setStatus("PaymentCanceled");
            repository().save(payment);
        });
    }

    public static void cancelPayment(OrderCanceled orderCanceled) {
        repository().findByOrderId(orderCanceled.getOrderId()).ifPresent(payment -> {
            payment.setStatus("PaymentCanceled");
            repository().save(payment);
        });
    }

}
