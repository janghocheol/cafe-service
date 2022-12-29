package cafe.service.infra;

import cafe.service.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @RequestMapping(value="/cafeOrders")
@Transactional
public class CafeOrderController {

    @Autowired
    CafeOrderRepository cafeOrderRepository;

    @RequestMapping(
        value = "cafeOrders/{id}/makestart",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CafeOrder makeStart(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /cafeOrder/makeStart  called #####");
        Optional<CafeOrder> optionalCafeOrder = cafeOrderRepository.findById(
            id
        );

        optionalCafeOrder.orElseThrow(() -> new Exception("No Entity Found"));
        CafeOrder cafeOrder = optionalCafeOrder.get();
        cafeOrder.makeStart();

        cafeOrderRepository.save(cafeOrder);
        return cafeOrder;
    }

    @RequestMapping(
        value = "cafeOrders/{id}/makecomplete",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CafeOrder makeComplete(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /cafeOrder/makeComplete  called #####");
        Optional<CafeOrder> optionalCafeOrder = cafeOrderRepository.findById(
            id
        );

        optionalCafeOrder.orElseThrow(() -> new Exception("No Entity Found"));
        CafeOrder cafeOrder = optionalCafeOrder.get();
        cafeOrder.makeComplete();

        cafeOrderRepository.save(cafeOrder);
        return cafeOrder;
    }

    @RequestMapping(
        value = "cafeOrders/{id}/orderapprove",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CafeOrder orderApprove(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /cafeOrder/orderApprove  called #####");
        Optional<CafeOrder> optionalCafeOrder = cafeOrderRepository.findById(
            id
        );

        optionalCafeOrder.orElseThrow(() -> new Exception("No Entity Found"));
        CafeOrder cafeOrder = optionalCafeOrder.get();
        cafeOrder.orderApprove();

        cafeOrderRepository.save(cafeOrder);
        return cafeOrder;
    }

    @RequestMapping(
        value = "cafeOrders/{id}/ordercancel",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CafeOrder orderCancel(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /cafeOrder/orderCancel  called #####");
        Optional<CafeOrder> optionalCafeOrder = cafeOrderRepository.findById(
            id
        );

        optionalCafeOrder.orElseThrow(() -> new Exception("No Entity Found"));
        CafeOrder cafeOrder = optionalCafeOrder.get();
        cafeOrder.orderCancel();

        cafeOrderRepository.save(cafeOrder);
        return cafeOrder;
    }
}
