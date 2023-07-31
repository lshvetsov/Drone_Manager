package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.model.mapper.OrderMapper;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.repository.OrderRepository;
import ge.shvetsov.dronemanager.utilities.DroneManagerExceptionHandler;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DroneRepository droneRepository;
    private final OrderMapper orderMapper;
    @Override
    public OrderDto getOrderDetails(String orderNum) {
        OrderEntity orderEntity = orderRepository.findByNumber(orderNum);
        return orderMapper.toDto(orderEntity);
    }

    @Override
    public OrderDto saveOrder(OrderDto order) {
        OrderEntity orderEntity = orderRepository.save(orderMapper.toEntity(order));
        return orderMapper.toDto(orderEntity);
    }

    @Override
    public Optional<OrderEntity> getActiveOrdersForDrone(String droneId) {
        return Optional.ofNullable(
                orderRepository.findByDrone_IdAndStateIsNotIn(droneId, List.of(OrderState.CANCELLED, OrderState.DELIVERED)));
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNum) {
        OrderEntity order = orderRepository.findByNumber(orderNum);
        if (order == null) throw DroneManagerExceptionHandler.exceptions.get("MS7");
        order.setState(OrderState.CANCELLED);
        orderRepository.save(order);

        Optional<DroneEntity> droneById = droneRepository.findById(order.getDrone().getId());

        if (droneById.isPresent()) {
            DroneEntity drone = droneById.get();
            drone.setState(DroneState.IDLE);
            droneRepository.save(drone);
        }
    }
    @Override
    public void switchOrderState(OrderEntity order, OrderState state) {
        order.setState(state);
        orderRepository.save(order);
    }
}
