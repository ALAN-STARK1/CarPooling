package org.example.carpooling.service;

import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;
import org.springframework.stereotype.Service;

@Service
public interface RouterService {
    Result submitOrder(SubmitOrderRequest submitOrderRequest);
}
