package ge.shvetsov.dronemanager.controller;

import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderNum}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable String orderNum) {
        OrderDto order = orderService.getOrderDetails(orderNum);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{orderNum}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderNum) {
        orderService.cancelOrder(orderNum);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
