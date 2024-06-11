package com.javatechie.os.api.service;

import com.javatechie.os.api.common.Payment;
import com.javatechie.os.api.common.TransactionRequest;
import com.javatechie.os.api.common.TransactionResponse;
import com.javatechie.os.api.entity.Order;
import com.javatechie.os.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
@Configuration
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String ENDPOINT_URL;

    public TransactionResponse saveOrder(TransactionRequest request) {
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        // rest call
        Payment paymentResponse = restTemplate.postForObject(ENDPOINT_URL,
                payment, payment.getClass());
        response = paymentResponse.getPaymentStatus().equals("success") ? "payment processing successful and order placed" :
                "there is a failure in payment api";
        orderRepository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }
}
