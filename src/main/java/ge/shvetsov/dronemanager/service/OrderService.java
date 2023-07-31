package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.utilities.enums.OrderState;

import java.util.Optional;

public interface OrderService {

    OrderDto getOrderDetails(String orderNum);
    OrderDto saveOrder(OrderDto order);
    Optional<OrderEntity> getActiveOrdersForDrone(String droneId);
    void cancelOrder(String orderNum);
    void switchOrderState(OrderEntity order, OrderState state);

}
