package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.reportdto.CashFlowStatmentReportResponseDTO;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.messages.ReportMessages;
import com.meiorganizadinho.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void postRevenueReportShouldReturn400WhenStartDateIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "",
                    "endDate": "2025-11-19"
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startDate is required"));

    }

    @Test
    void postRevenueReportShouldReturn400WhenStartDateIsNull() throws Exception {
        String jsonRequest = """
                {
                    "startDate": ,
                    "endDate": "2025-11-19"
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void postRevenueReportShouldReturn400WhenStartDateDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "tartDate": "2025-11-19",
                    "endDate": "2025-11-19"
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("startDate is required"));
    }

    @Test
    void postRevenueReportShouldReturn400WhenEndDateIsEmpty() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "2025-11-19",
                    "endDate": ""
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endDate is required"));

    }

    @Test
    void postRevenueReportShouldReturn400WhenEndDateIsNull() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "2025-11-19",
                    "endDate": 
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("Invalid request body"));

    }

    @Test
    void postRevenueReportShouldReturn400WhenEndDateDoesNotExists() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "2025-11-19",
                    "ndDate": "2025-11-19"
                }
                """;

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endDate is required"));
    }

    @Test
    void postRevenueReportShouldReturn400WhenPeriodIsGreaterThanOneYear() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "2025-11-19",
                    "endDate": "2027-11-19"
                }
                """;
    when(reportService.revenueReport(LocalDate.of(2025, 11, 19), LocalDate.of(2027, 11, 19)))
            .thenThrow(new BusinessException(ReportMessages.LESS_THAN_ONE_YEAR_MESSAGE));

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("The reporting period should be a maximum of 1 year."));
    }

    @Test
    void postRevenueReportShouldReturn400WhenEndDateIsBeforeStartDate() throws Exception {
        String jsonRequest = """
                {
                    "startDate": "2028-11-19",
                    "endDate": "2027-11-19"
                }
                """;
        when(reportService.revenueReport(LocalDate.of(2028, 11, 19), LocalDate.of(2027, 11, 19)))
                .thenThrow(new BusinessException(ReportMessages.ENDDATE_CANNOT_BE_BEFORE_STARTDATE));

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endDate cannot be before startDate"));
    }

    @Test
    void postRevenueReportShouldReturnRevenueReport() throws Exception {
        CashFlowStatmentReportResponseDTO mockResponse = new CashFlowStatmentReportResponseDTO();

        String jsonRequest = """
                {
                    "startDate": "2025-11-19",
                    "endDate": "2025-11-19"
                }
                """;

        when(reportService.revenueReport(LocalDate.of(2025, 11, 19), LocalDate.of(2025, 11, 19)))
                .thenThrow(new BusinessException(ReportMessages.ENDDATE_CANNOT_BE_BEFORE_STARTDATE));

        mockMvc.perform(post("/api/v1/reports/revenue-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("endDate cannot be before startDate"));
    }

}
