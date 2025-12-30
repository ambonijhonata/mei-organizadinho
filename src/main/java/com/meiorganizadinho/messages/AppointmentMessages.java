package com.meiorganizadinho.messages;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentMessages {
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found";
    public static final String START_TIME_CANNOT_BE_AFTER_END_TIME = "Start time cannot be after end time";
    public static final String AT_LEAST_ONE_SERVICE_IS_REQUIRED = "At least one service is required";
    private static final String CONFLICTING_APPOINTMENTS = "Conflicting appointments in date %s between %s and %s are found";

    public static String getConflictingAppointmentsMessage(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return String.format(CONFLICTING_APPOINTMENTS, date, startTime, endTime);
    }
}
