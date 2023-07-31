package ge.shvetsov.dronemanager.repository;

import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    OrderEntity findByDrone_IdAndStateIsNotIn(String id, List<OrderState> orderStates);
    OrderEntity findByNumber(String orderNum);
}
