package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.model.mapper.DroneMapper;
import ge.shvetsov.dronemanager.model.mapper.MedicationMapper;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.utilities.DroneManagerExceptionHandler;
import ge.shvetsov.dronemanager.utilities.enums.DroneModel;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import ge.shvetsov.dronemanager.utilities.exception.ApplicationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private OrderService orderService;
    @Mock
    private DroneMapper droneMapper;
    @Mock
    private MedicationMapper medicationMapper;

    @InjectMocks
    private DroneServiceImpl droneService;
    private DroneEntity droneEntity;
    private OrderEntity orderEntity;
    private DroneDto droneDto;
    private OrderDto orderDto;
    private List<MedicationEntity> medicationEntities;
    private List<MedicationDto> medicationDtos;

    private static final int MIN_CHARGING_LEVEL = 25;

    @BeforeEach
    void setUp() {
        droneEntity = TestUtilities.getDroneEntity();
        orderEntity = TestUtilities.getOrderEntity(droneEntity);
        droneDto = TestUtilities.getDroneDto();
        orderDto = TestUtilities.getOrderDto(droneDto);
        medicationEntities = List.of(TestUtilities.getMedicationEntity(), TestUtilities.getMedicationEntity());
        medicationDtos = List.of(TestUtilities.getMedicationDto(), TestUtilities.getMedicationDto());
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Register drone. Sunny day")
    void registerNewProne() {
        when(droneRepository.save(any(DroneEntity.class))).thenReturn(droneEntity);
        when(droneMapper.toEntity(droneDto)).thenReturn(droneEntity);
        when(droneMapper.toDto(droneEntity)).thenReturn(droneDto);
        Assertions.assertEquals(droneDto, droneService.registerNewProne(droneDto));
    }

    @Test
    @DisplayName("Get Drone. Sunny day")
    void getDrone() {
        String id = droneEntity.getId();
        when(droneRepository.findById(id)).thenReturn(Optional.of(droneEntity));
        when(droneMapper.toDto(droneEntity)).thenReturn(droneDto);

        Assertions.assertEquals(droneDto, droneService.getDrone(id));
    }

    @Test
    @DisplayName("Get Drone. Drone not found")
    void getDroneNotFound() {
        String id = droneEntity.getId();
        when(droneRepository.findById(id)).thenReturn(Optional.empty());
        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.getDrone(id));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS1"), applicationException);
    }

    @Test
    @DisplayName("Get Drones. Sunny day")
    void getDrones() {
        when(droneRepository.findAll()).thenReturn(List.of(droneEntity, droneEntity));
        when(droneMapper.toDto(droneEntity)).thenReturn(droneDto);
        Assertions.assertEquals(droneDto, droneService.getDrones().get(0));
    }

    @Test
    @DisplayName("Is drone available. Idle drone")
    void isDroneAvailableIdleDrone() {
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.ofNullable(droneEntity));
        Assertions.assertTrue(droneService.isDroneAvailable(droneEntity.getId()));
    }

    @ParameterizedTest
    @EnumSource(names = { "LOADING", "LOADED", "DELIVERING", "DELIVERED", "RETURNING"})
    @DisplayName("Is drone available. Not Idle drone")
    void isDroneAvailable(DroneState state) {
        droneEntity.setState(state);
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.ofNullable(droneEntity));
        Assertions.assertFalse(droneService.isDroneAvailable(droneEntity.getId()));
    }


    @Test
    @DisplayName("Get Drone battery. Sunny day")
    void getDroneBattery() {
        int batteryLevel = 58;
        String id = droneEntity.getId();
        droneEntity.setBatteryCapacity(batteryLevel);
        when(droneRepository.findById(id)).thenReturn(Optional.of(droneEntity));
        Assertions.assertEquals(batteryLevel, droneService.getDroneBattery(id));
    }

    @Test
    @DisplayName("Get Drone. Drone not found")
    void getDroneBatteryNotFound() {
        String id = droneEntity.getId();
        when(droneRepository.findById(id)).thenReturn(Optional.empty());
        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.getDroneBattery(id));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS1"), applicationException);
    }

    @Test
    @DisplayName("Load Drone. Sunny day")
    void loadDroneWithMedications() {
        droneService.setMinChargingLevel(MIN_CHARGING_LEVEL);
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.saveOrder(any(OrderDto.class))).thenReturn(orderDto);
        when(droneMapper.toDto(droneEntity)).thenReturn(droneDto);

        Assertions.assertEquals(orderEntity.getNumber(), droneService.loadDroneWithMedications(droneId, medicationDtos));
    }

    @Test
    @DisplayName("Load Drone. Drone is not on the base")
    void loadDroneWithMedicationsNotIdle() {
        droneService.setMinChargingLevel(MIN_CHARGING_LEVEL);
        droneEntity.setState(DroneState.DELIVERING);
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.saveOrder(any(OrderDto.class))).thenReturn(orderDto);

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.loadDroneWithMedications(droneId, medicationDtos));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS4"), applicationException);
    }

    @Test
    @DisplayName("Load Drone. Drone is not on the base")
    void loadDroneWithMedicationsLowCharge() {
        droneService.setMinChargingLevel(MIN_CHARGING_LEVEL);
        droneEntity.setBatteryCapacity(20);
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.saveOrder(any(OrderDto.class))).thenReturn(orderDto);

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.loadDroneWithMedications(droneId, medicationDtos));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS2"), applicationException);
    }

    @Test
    @DisplayName("Load Drone. Medication overload")
    void loadDroneWithMedicationsTooManyMedications() {
        droneService.setMinChargingLevel(MIN_CHARGING_LEVEL);
        medicationDtos = List.of(TestUtilities.getMedicationDto(), TestUtilities.getMedicationDto(), TestUtilities.getMedicationDto());
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.loadDroneWithMedications(droneId, medicationDtos));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS3"), applicationException);
    }

    @Test
    @DisplayName("Load Drone. Drone is not found")
    void loadDroneWithMedicationDroneNotFound() {
        droneService.setMinChargingLevel(MIN_CHARGING_LEVEL);
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.empty());
        when(orderService.saveOrder(any(OrderDto.class))).thenReturn(orderDto);

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.loadDroneWithMedications(droneId, medicationDtos));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS1"), applicationException);
    }

    @Test
    @DisplayName("Get Medications for Drone. Sunny day")
    void getMedicationsForDrone() {
        orderEntity.setMedications(medicationEntities);
        var medication1 = medicationEntities.get(0);
        var medication2 = medicationEntities.get(1);
        var medicationDto1 = medicationDtos.get(0);
        var medicationDto2 = medicationDtos.get(1);
        when(orderService.getActiveOrdersForDrone(droneEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(medicationMapper.toDto(medication1)).thenReturn(medicationDto1);
        when(medicationMapper.toDto(medication2)).thenReturn(medicationDto2);
        Assertions.assertEquals(medicationDtos, droneService.getMedicationsForDrone(droneEntity.getId()));
    }

    @Test
    @DisplayName("Launch Drone. Sunny day")
    void launchDrone() {
        String droneId = droneEntity.getId();
        droneEntity.setState(DroneState.LOADED);
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.getActiveOrdersForDrone(droneId)).thenReturn(Optional.of(orderEntity));

        Assertions.assertEquals(orderEntity.getNumber(), droneService.launchDrone(droneId));
    }

    @Test
    @DisplayName("Launch Drone. Drone is not ready")
    void launchDroneNotReady() {
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.getActiveOrdersForDrone(droneId)).thenReturn(Optional.of(orderEntity));

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.launchDrone(droneId));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS5"), applicationException);
    }

    @Test
    @DisplayName("Launch Drone. Drone is not found")
    void launchDroneNotFound() {
        String droneId = droneEntity.getId();
        when(droneRepository.findById(droneId)).thenReturn(Optional.empty());
        when(orderService.getActiveOrdersForDrone(droneId)).thenReturn(Optional.of(orderEntity));

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.launchDrone(droneId));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS1"), applicationException);
    }

    @Test
    @DisplayName("Launch Drone. Order is not found")
    void launchDroneOrderNotFound() {
        String droneId = droneEntity.getId();
        droneEntity.setState(DroneState.LOADED);
        when(droneRepository.findById(droneId)).thenReturn(Optional.ofNullable(droneEntity));
        when(orderService.getActiveOrdersForDrone(droneId)).thenReturn(Optional.empty());

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.launchDrone(droneId));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS7"), applicationException);
    }

    @Test
    @DisplayName("Receive drone. Sunny day")
    void receiveDrone() {
        droneEntity.setState(DroneState.DELIVERING);
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.of(droneEntity));
        when(orderService.getActiveOrdersForDrone(droneEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(droneMapper.toDto(droneEntity)).thenReturn(droneDto);

        Assertions.assertEquals(DroneState.IDLE, droneService.receiveDrone(droneEntity.getId()).getState());
    }

    @Test
    @DisplayName("Receive drone. Wrong drone status")
    void receiveDroneWrongStatus() {
        String droneId = droneEntity.getId();
        droneEntity.setState(DroneState.LOADED);
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.of(droneEntity));
        when(orderService.getActiveOrdersForDrone(droneEntity.getId())).thenReturn(Optional.of(orderEntity));

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.receiveDrone(droneId));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS6"), applicationException);
    }

    @Test
    @DisplayName("Update drone. Sunny day")
    void updateDrone() {
        droneEntity.setModel(DroneModel.HEAVYWEIGHT);
        DroneDto expectedDrone = DroneDto.builder()
                .id(droneEntity.getId())
                .serialNumber(droneEntity.getSerialNumber())
                .model(DroneModel.HEAVYWEIGHT)
                .weightLimit(BigDecimal.valueOf(120L))
                .batteryCapacity(100)
                .state(DroneState.IDLE)
                .build();
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.of(droneEntity));
        when(droneRepository.save(any(DroneEntity.class))).thenReturn(droneEntity);
        when(droneMapper.toEntity(droneDto)).thenReturn(droneEntity);
        when(droneMapper.toDto(droneEntity)).thenReturn(expectedDrone);
        Assertions.assertEquals(DroneModel.HEAVYWEIGHT, droneService.updateDrone(droneDto).getModel());
    }

    @Test
    @DisplayName("Update drone. Drone not found")
    void updateDroneNotFound() {
        droneEntity.setModel(DroneModel.HEAVYWEIGHT);
        when(droneRepository.findById(droneEntity.getId())).thenReturn(Optional.empty());
        when(droneRepository.save(any(DroneEntity.class))).thenReturn(droneEntity);

        ApplicationException applicationException =
                Assertions.assertThrows(ApplicationException.class, () -> droneService.updateDrone(droneDto));
        Assertions.assertEquals(DroneManagerExceptionHandler.exceptions.get("MS1"), applicationException);
    }

    @Test
    @DisplayName("Switch drone state. Sunny day")
    void switchDroneState() {
        droneEntity.setState(DroneState.LOADING);
        droneService.switchDroneState(droneEntity, DroneState.LOADED);
        Assertions.assertEquals(DroneState.LOADED, droneEntity.getState());
    }
}