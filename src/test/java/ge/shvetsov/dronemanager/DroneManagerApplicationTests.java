package ge.shvetsov.dronemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.service.DroneService;
import ge.shvetsov.dronemanager.service.MedicationService;
import ge.shvetsov.dronemanager.service.OrderService;
import ge.shvetsov.dronemanager.service.TestUtilities;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.runtime.ObjectMethods;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DroneManagerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DroneService droneService;

	@Autowired
	private MedicationService medicationService;

	@Autowired
	private OrderService orderService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void basicFlow() throws Exception {

		String droneId = "d40aad80-b87a-4424-9dcf-f0748d553a46";
		List<String> medicineCodes = List.of("h09p4Ng", "KR60");

		// 1. Get medicines

		MvcResult medicineResponse = mockMvc.perform(MockMvcRequestBuilders.post("/medication/list")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(medicineCodes)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		List<MedicationDto> medicines = (List<MedicationDto>) objectMapper.readValue(medicineResponse.getResponse().getContentAsString(), List.class);

		Assertions.assertEquals(2, medicines.size());

		// 2. Load drone

		MvcResult loadResponse = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/drones/%s/load", droneId))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(medicines)))
				.andExpect(status().isOk())
				.andReturn();

		String loadOrderId = loadResponse.getResponse().getContentAsString();

		Assertions.assertNotNull(loadOrderId);

		// 3. Check the drone load

		MvcResult loadMedicationsResponse = mockMvc.perform(
				MockMvcRequestBuilders.get(String.format("/drones/%s/medications", droneId)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		Assertions.assertEquals(2,
				objectMapper.readValue(loadMedicationsResponse.getResponse().getContentAsString(), List.class).size());

		// 4. Launch drone

		MvcResult launchResponse = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/drones/%s/launch", droneId)))
				.andExpect(status().isOk())
				.andReturn();

		String launchOrderId = launchResponse.getResponse().getContentAsString();

		Assertions.assertEquals(loadOrderId, launchOrderId);

		// 5. Check the drone state and availability

		MvcResult droneStatusResponse = mockMvc.perform(
						MockMvcRequestBuilders.get(String.format("/drones/%s", droneId)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		DroneDto drone = objectMapper.readValue(droneStatusResponse.getResponse().getContentAsString(), DroneDto.class);
		Assertions.assertEquals(DroneState.DELIVERING, drone.getState());

		mockMvc.perform(MockMvcRequestBuilders.get(String.format("/drones/%s/available", droneId)))
				.andExpect(status().is4xxClientError()).andReturn();

		// 6. Receive the drone (the order has been delivered)

		MvcResult receiveResponse = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/drones/%s/receive", droneId)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		DroneDto receivedDrone = objectMapper.readValue(receiveResponse.getResponse().getContentAsString(), DroneDto.class);

		Assertions.assertEquals(drone.getId(), receivedDrone.getId());

	}

}
