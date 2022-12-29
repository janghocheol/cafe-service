package cafe.service.external;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    /**
     * Fallback
     */
    @Override
    public void startPayment(Payment payment) {
        throw new RuntimeException("Out of Payment Service!  Try in a while ");
    }
}
