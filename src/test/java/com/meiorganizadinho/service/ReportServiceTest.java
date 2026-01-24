package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.reportdto.CashFlowStatmentReportResponseDTO;
import com.meiorganizadinho.dto.reportdto.RevenueReportResponseDTO;
import com.meiorganizadinho.entity.CashFlowStatement;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void revenueReportShouldThrowBusinessExceptionWhenPeriodIsGreaterThanOneYear() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 2);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                reportService.revenueReport(startDate, endDate)
        );

        String expectedMessage = "The reporting period should be a maximum of 1 year.";
        assert(businessException.getMessage().equals(expectedMessage));

        verify(appointmentRepository, never()).getTotalValueByDateRange(startDate, endDate);
    }

    @Test
    void revenueReportShouldThrowBusinessExceptionWhenEndDateIsBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                reportService.revenueReport(startDate, endDate)
        );

        String expectedMessage = "endDate cannot be before startDate";
        assert(businessException.getMessage().equals(expectedMessage));

        verify(appointmentRepository, never()).getTotalValueByDateRange(startDate, endDate);
    }

    @Test
    void revenueReportShouldReturnRevenueReport() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Double totalValue = 215.98;
        when(appointmentRepository.getTotalValueByDateRange(startDate, endDate)).thenReturn(totalValue);

        RevenueReportResponseDTO responseDTO = reportService.revenueReport(startDate, endDate);

        verify(appointmentRepository).getTotalValueByDateRange(startDate, endDate);

        assertEquals(totalValue, responseDTO.value());
    }

    @Test
    void cashFlowStatmentReportShouldThrowBusinessExceptionWhenPeriodIsGreaterThanOneYear() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 2);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                reportService.cashFlowStatmentReport(startDate, endDate)
        );

        String expectedMessage = "The reporting period should be a maximum of 1 year.";
        assert(businessException.getMessage().equals(expectedMessage));

        verify(appointmentRepository, never()).getDailyCashFlowReport(startDate, endDate);
    }

    @Test
    void cashFlowStatmentReportShouldThrowBusinessExceptionWhenEndDateIsBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                reportService.cashFlowStatmentReport(startDate, endDate)
        );

        String expectedMessage = "endDate cannot be before startDate";
        assert(businessException.getMessage().equals(expectedMessage));

        verify(appointmentRepository, never()).getDailyCashFlowReport(startDate, endDate);
    }

    @Test
    void cashFlowStatmentReportShouldReturnCashFlowStatmentReport() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        List<CashFlowStatement> mockCashFlowData = List.of(
                new CashFlowStatement() {
                    @Override
                    public LocalDate getAppointmentDate() {
                        return LocalDate.of(2025, 1, 15);
                    }

                    @Override
                    public Double getTotalValue() {
                        return 150.50;
                    }
                },
                new CashFlowStatement() {
                    @Override
                    public LocalDate getAppointmentDate() {
                        return LocalDate.of(2025, 1, 16);
                    }

                    @Override
                    public Double getTotalValue() {
                        return 200.00;
                    }
                },
                new CashFlowStatement() {
                    @Override
                    public LocalDate getAppointmentDate() {
                        return LocalDate.of(2025, 2, 1);
                    }

                    @Override
                    public Double getTotalValue() {
                        return 75.25;
                    }
                }
        );

        when(appointmentRepository.getDailyCashFlowReport(startDate, endDate)).thenReturn(mockCashFlowData);
        CashFlowStatmentReportResponseDTO cashFlowStatmentReportResponseDTO = reportService.cashFlowStatmentReport(startDate, endDate);

        for(int i = 0; i < mockCashFlowData.size(); i++) {
            assertEquals(mockCashFlowData.get(i).getAppointmentDate(),
                    cashFlowStatmentReportResponseDTO.report().get(i).getAppointmentDate());
            assertEquals(mockCashFlowData.get(i).getTotalValue(),
                    cashFlowStatmentReportResponseDTO.report().get(i).getTotalValue());
        }

        verify(appointmentRepository).getDailyCashFlowReport(startDate, endDate);
    }
}
