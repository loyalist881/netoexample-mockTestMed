package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

class MedicalServiceImplTest {
    @Test
    void checkBloodPressure() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        String id = "id-1";
        PatientInfo patientInfo = new PatientInfo(
                id, "Ivan", "Petrov", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))
        );
        when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkBloodPressure(id, new BloodPressure(130, 90));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(patientInfoFileRepository).getById(id);
        Mockito.verify(sendAlertService).send(captor.capture());
        Assertions.assertEquals(
                "Warning, patient with id: id-1, need help",
                captor.getValue()
        );
    }

    @Test
    void checkTemperature_shouldSendWarning_whenTemperatureTooLow() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        String id = "id-2";
        PatientInfo patientInfo = new PatientInfo(
                id, "Semen", "Mihailov", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))
        );
        when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkTemperature(id, new BigDecimal("34.9"));

        Mockito.verify(patientInfoFileRepository).getById(id);
        Mockito.verify(sendAlertService).send("Warning, patient with id: id-2, need help");
        Mockito.verifyNoMoreInteractions(sendAlertService);
    }

    @Test
    void checkBloodPressure_shouldNotSendWarning_whenPressureIsNormal() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        String id = "id-3";
        BloodPressure normal = new BloodPressure(120, 80);
        PatientInfo patientInfo = new PatientInfo(
                id, "A", "B", LocalDate.of(2000, 1, 1),
                new HealthInfo(new BigDecimal("36.6"), normal)
        );
        when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkBloodPressure(id, new BloodPressure(120, 80));

        Mockito.verify(patientInfoFileRepository).getById(id);
        Mockito.verifyNoInteractions(sendAlertService);
    }

    @Test
    void checkTemperature_shouldNotSendWarning_whenTemperatureIsNormal() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        String id = "id-4";
        PatientInfo patientInfo = new PatientInfo(
                id, "A", "B", LocalDate.of(2000, 1, 1),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80))
        );
        when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);

        medicalService.checkTemperature(id, new BigDecimal("35.1"));

        Mockito.verify(patientInfoFileRepository).getById(id);
        Mockito.verifyNoInteractions(sendAlertService);
    }
}
