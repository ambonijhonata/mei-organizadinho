package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.reportdto.CashFlowStatmentReportResponseDTO;
import com.meiorganizadinho.dto.reportdto.RevenueReportResponseDTO;
import com.meiorganizadinho.entity.CashFlowStatement;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ReportService {
    private AppointmentRepository appointmentRepository;
    private static final String LESS_THAN_ONE_YEAR_MESSAGE = "The reporting period should be a maximum of 1 year.";

    public ReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public RevenueReportResponseDTO revenueReport(LocalDate startDate, LocalDate endDate) {
        validateReportRequestDates(startDate, endDate);

        Double totalValue = appointmentRepository.getTotalValueByDateRange(startDate, endDate);
        return new RevenueReportResponseDTO(totalValue);
    }

    public CashFlowStatmentReportResponseDTO cashFlowStatmentReport(LocalDate startDate, LocalDate endDate) {
        validateReportRequestDates(startDate, endDate);

        List<CashFlowStatement> report = appointmentRepository.getDailyCashFlowReport(startDate, endDate);
        return new CashFlowStatmentReportResponseDTO(report);
    }

    public void validateReportRequestDates(LocalDate startDate, LocalDate endDate){
        validatePeriodLessThanOneYear(startDate, endDate);
        validateEndDateBeforeStartDate(startDate, endDate);
    }

    private void validatePeriodLessThanOneYear(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        boolean isLessThanOneYear = period.getYears() < 1 ||
                (period.getYears() == 1 && period.getMonths() == 0 && period.getDays() == 0);

        if(!isLessThanOneYear) {
            throw new BusinessException(LESS_THAN_ONE_YEAR_MESSAGE);
        }
    }

    private void validateEndDateBeforeStartDate(LocalDate startDate, LocalDate endDate){
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("endDate cannot be before startDate");
        }
    }
}
