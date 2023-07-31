package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import ge.shvetsov.dronemanager.model.mapper.MedicationMapper;
import ge.shvetsov.dronemanager.repository.MedicationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicationServiceImplTest {

    @Mock
    private MedicationRepository medicationRepository;
    @Mock
    private MedicationMapper medicationMapper;

    @InjectMocks
    private MedicationServiceImpl medicationService;

    public MedicationServiceImplTest() {
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetMedicationById() {
        MedicationDto medicationDto = TestUtilities.getMedicationDto();
        String medicationId = medicationDto.getId();
        MedicationEntity meditationEntity = TestUtilities.getMedicationEntity();

        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(meditationEntity));
        when(medicationMapper.toDto(meditationEntity)).thenReturn(medicationDto);

        Assertions.assertEquals(medicationDto, medicationService.getMedicationById(medicationId));
    }

    @Test
    void testGetMedicationByCode() {
        MedicationDto me = TestUtilities.getMedicationDto();
        String medicationCode = me.getCode();
        MedicationEntity meditationEntity = TestUtilities.getMedicationEntity();

        when(medicationRepository.findByCode(medicationCode)).thenReturn(meditationEntity);
        when(medicationMapper.toDto(meditationEntity)).thenReturn(me);

        Assertions.assertEquals(me, medicationService.getMedicationByCode(medicationCode));
    }

    @Test
    void testGetMedications() {
        var medicationEntity1 = TestUtilities.getMedicationEntity();
        var medicationEntity2 = TestUtilities.getMedicationEntity();
        List<MedicationEntity> entities = List.of(medicationEntity1, medicationEntity2);
        var medicationDto1 = TestUtilities.getMedicationDto();
        var medicationDto2 = TestUtilities.getMedicationDto();
        List<MedicationDto> dtos = List.of(medicationDto1, medicationDto2);
        List<String> medicationCodes = List.of(dtos.get(0).getCode(), dtos.get(1).getCode());

        when(medicationRepository.findAllByCodeIn(medicationCodes)).thenReturn(entities);
        when(medicationMapper.toDto(medicationEntity1)).thenReturn(medicationDto1);
        when(medicationMapper.toDto(medicationEntity2)).thenReturn(medicationDto2);

        Assertions.assertEquals(dtos, medicationService.getMedications(medicationCodes));
    }

    @Test
    void testGetMedicationsLessThan() {
        BigDecimal weight = BigDecimal.valueOf(10);

        var medicationEntity1 = TestUtilities.getMedicationEntity();
        var medicationEntity2 = TestUtilities.getMedicationEntity();
        List<MedicationEntity> entities = List.of(medicationEntity1, medicationEntity2);
        var medicationDto1 = TestUtilities.getMedicationDto();
        var medicationDto2 = TestUtilities.getMedicationDto();
        List<MedicationDto> dtos = List.of(medicationDto1, medicationDto2);

        when(medicationRepository.findAllByWeightLessThanEqual(weight)).thenReturn(entities);
        when(medicationMapper.toDto(medicationEntity1)).thenReturn(medicationDto1);
        when(medicationMapper.toDto(medicationEntity2)).thenReturn(medicationDto2);

        List<MedicationDto> result = medicationService.getMedicationsLessThan(weight);

        Assertions.assertEquals(dtos, result);
    }

    @Test
    void testCreateMedication() {
        MedicationEntity medicationEntity = TestUtilities.getMedicationEntity();
        MedicationDto dto = TestUtilities.getMedicationDto();
        when(medicationRepository.save(any(MedicationEntity.class))).thenReturn(medicationEntity);
        when(medicationMapper.toEntity(dto)).thenReturn(medicationEntity);
        when(medicationMapper.toDto(medicationEntity)).thenReturn(dto);

        MedicationDto result = medicationService.createMedication(dto);

        Assertions.assertEquals(dto, result);
        verify(medicationRepository, times(1)).save(medicationEntity);
    }

    @Test
    void testUpdateMedication() {
        MedicationDto dto = TestUtilities.getMedicationDto();
        String medicationId = dto.getId();
        MedicationEntity meditationEntity = TestUtilities.getMedicationEntity();

        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(meditationEntity));
        when(medicationRepository.save(any(MedicationEntity.class))).thenReturn(meditationEntity);
        when(medicationMapper.toEntity(dto)).thenReturn(meditationEntity);
        when(medicationMapper.toDto(meditationEntity)).thenReturn(dto);

        MedicationDto result = medicationService.updateMedication(dto);

        Assertions.assertEquals(dto, result);
        verify(medicationRepository, times(1)).save(meditationEntity);
    }

    @Test
    void testUpdateMedicationWithNoSuchElementException() {
        MedicationDto dto = TestUtilities.getMedicationDto();
        String medicationId = dto.getId();

        when(medicationRepository.findById(medicationId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> medicationService.updateMedication(dto));

        verify(medicationRepository, never()).save(any(MedicationEntity.class));
    }

    @Test
    void testDeleteMedication() {
        String medicationId = "1";

        medicationService.deleteMedication(medicationId);

        verify(medicationRepository, times(1)).deleteById(medicationId);
    }

}
