package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.model.mapper.OrderMapper;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.repository.OrderRepository;
import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import ge.shvetsov.dronemanager.utilities.exception.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DroneRepository droneRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getOrderDetails() {
        var orderEntity = TestUtilities.getOrderEntity(TestUtilities.getDroneEntity());
        var orderDto = TestUtilities.getOrderDto(TestUtilities.getDroneDto());
        String orderNumber = orderEntity.getNumber();

        when(orderRepository.findByNumber(orderNumber)).thenReturn(orderEntity);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderDto);

        Assertions.assertEquals(orderDto, orderService.getOrderDetails(orderNumber));
    }

    @Test
    void saveOrder() {
        OrderEntity orderEntity = TestUtilities.getOrderEntity(TestUtilities.getDroneEntity());
        OrderDto orderDto = TestUtilities.getOrderDto(TestUtilities.getDroneDto());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderDto);
        when(orderMapper.toEntity(orderDto)).thenReturn(orderEntity);

        OrderDto result = orderService.saveOrder(orderDto);

        Assertions.assertEquals(orderDto, result);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("Get active Order for Drone. Sunny day")
    void getActiveOrdersForDrone() {
        DroneEntity drone = TestUtilities.getDroneEntity();
        OrderEntity expectedOrder = TestUtilities.getOrderEntity(drone);

        when(orderRepository.findByDrone_IdAndStateIsNotIn(drone.getId(), List.of(OrderState.CANCELLED, OrderState.DELIVERED)))
                .thenReturn(expectedOrder);

        Optional<OrderEntity> result = orderService.getActiveOrdersForDrone(drone.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedOrder, result.get());
    }

    @Test
    @DisplayName("Get active Order for Drone. Cancelled order")
    void getActiveOrdersForDroneOrderNotFound() {
        DroneEntity drone = TestUtilities.getDroneEntity();

        when(orderRepository.findByDrone_IdAndStateIsNotIn(drone.getId(), List.of(OrderState.CANCELLED, OrderState.DELIVERED)))
                .thenReturn(null);

        Optional<OrderEntity> result = orderService.getActiveOrdersForDrone(drone.getId());

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Cancel order. Sunny day")
    void testCancelOrder() {
        DroneEntity drone = TestUtilities.getDroneEntity();
        OrderEntity order = TestUtilities.getOrderEntity(drone);
        String orderNumber = order.getNumber();

        when(orderRepository.findByNumber(orderNumber)).thenReturn(order);
        when(droneRepository.findById(drone.getId())).thenReturn(Optional.of(drone));

        orderService.cancelOrder(orderNumber);

        verify(orderRepository, times(1)).save(order);
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    @DisplayName("Cancel order. Order is not found")
    void testCancelOrderNotFound() {
        DroneEntity drone = TestUtilities.getDroneEntity();
        OrderEntity order = TestUtilities.getOrderEntity(drone);
        String orderNumber = order.getNumber();

        when(orderRepository.findByNumber(orderNumber)).thenReturn(null);
        when(droneRepository.findById(drone.getId())).thenReturn(Optional.of(drone));

        Assertions.assertThrows(ApplicationException.class, () -> orderService.cancelOrder(orderNumber));

        verify(orderRepository, times(0)).save(order);
        verify(droneRepository, times(0)).save(drone);
    }

    @Test
    @DisplayName("Cancel order. Drone is not found")
    void testCancelOrderDroneNotFound() {
        DroneEntity drone = TestUtilities.getDroneEntity();
        OrderEntity order = TestUtilities.getOrderEntity(drone);
        String orderNumber = order.getNumber();

        when(orderRepository.findByNumber(orderNumber)).thenReturn(order);
        when(droneRepository.findById(drone.getId())).thenReturn(Optional.empty());

        orderService.cancelOrder(orderNumber);

        verify(orderRepository, times(1)).save(order);
        verify(droneRepository, times(0)).save(drone);
    }

    @Test
    void testSwitchOrderState() {
        OrderEntity order = new OrderEntity();
        OrderState newState = OrderState.DELIVERED;

        orderService.switchOrderState(order, newState);

        Assertions.assertEquals(newState, order.getState());
        verify(orderRepository, times(1)).save(order);
    }

}
