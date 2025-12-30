package com.meiorganizadinho.messages;

public class ServicesMessages {
    private static final String SERVICE_ALREADY_EXISTS = "Service with name %S already exists";
    private static final String SERVICE_NOT_FOUND = "Service %s not found";
    private static final String SERVICE_HAS_LINK_WITH_N_APPOINTMENTS = "Service has link with %s appointment(s)";

    public static String getServiceNotFoundMessage(Long id) {
        return String.format(SERVICE_NOT_FOUND, id);
    }

    public static String getServiceAlreadyExistsMessage(String serviceName) {
        return String.format(SERVICE_ALREADY_EXISTS, serviceName);
    }

    public static String getServiceHasLinkWithNAppointmentsMessage(int qtdAppointments){
        return String.format(SERVICE_HAS_LINK_WITH_N_APPOINTMENTS, qtdAppointments);
    }
}
