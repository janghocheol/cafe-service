package cafe.service.infra;

import cafe.service.domain.*;
import cafe.service.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderListViewHandler {

    @Autowired
    private OrderListRepository orderListRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderPlaced_then_CREATE_1 (@Payload OrderPlaced orderPlaced) {
        try {

            if (!orderPlaced.validate()) return;

            // view 객체 생성
            OrderList orderList = new OrderList();
            // view 객체에 이벤트의 Value 를 set 함
            orderList.setOrderId(orderPlaced.getOrderId());
            orderList.setCustumerId(orderPlaced.getCustomerId());
            orderList.setCafeId(orderPlaced.getCafeId());
            orderList.setMenuId(orderPlaced.getMenuId());
            orderList.setQty(orderPlaced.getQty());
            orderList.setTotalPrice(orderPlaced.getTotalPrice());
            orderList.setStatus("orderPlaced");
            orderList.setOrderDate(orderPlaced.getOrderDate());
            // view 레파지 토리에 save
            orderListRepository.save(orderList);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCustomerOrderCanceled_then_UPDATE_1(@Payload CustomerOrderCanceled customerOrderCanceled) {
        try {
            if (!customerOrderCanceled.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(customerOrderCanceled.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("OrderCanceled");
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_UPDATE_2(@Payload OrderCanceled orderCanceled) {
        try {
            if (!orderCanceled.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(orderCanceled.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("OrderCanceled");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderApproved_then_UPDATE_3(@Payload OrderApproved orderApproved) {
        try {
            if (!orderApproved.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(orderApproved.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("OrderApproved");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenMakeStarted_then_UPDATE_4(@Payload MakeStarted makeStarted) {
        try {
            if (!makeStarted.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(makeStarted.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("MakeStarted");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenMakeCompleted_then_UPDATE_5(@Payload MakeCompleted makeCompleted) {
        try {
            if (!makeCompleted.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(makeCompleted.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("MakeCompleted");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_UPDATE_6(@Payload Paid paid) {
        try {
            if (!paid.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(paid.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("Paid");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCanceled_then_UPDATE_7(@Payload PaymentCanceled paymentCanceled) {
        try {
            if (!paymentCanceled.validate()) return;
                // view 객체 조회
            Optional<OrderList> orderListOptional = orderListRepository.findById(paymentCanceled.getOrderId());

            if( orderListOptional.isPresent()) {
                 OrderList orderList = orderListOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderList.setStatus("PaymentCanceled");    
                // view 레파지 토리에 save
                 orderListRepository.save(orderList);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

