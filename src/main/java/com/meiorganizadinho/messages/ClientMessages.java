package com.meiorganizadinho.messages;

public class ClientMessages {
    public static final String CLIENT_NOT_FOUND = "Client not found";
    private static final String CLIENT_WITH_NAME_ALREADY_EXISTS = "Client with name %s already exists";
    private static final String CLIENT_HAS_LINK_WITH_N_APPOINTMENTS = "Client has link with %S appointment(s)";

    public static String getClientWithNameAlreadyExistsMessage(String clientName) {
        return String.format(CLIENT_WITH_NAME_ALREADY_EXISTS, clientName);
    }

    public static String getClientHasLinkWithNAppointmentsMessage(int qtdAppointments){
        return String.format(CLIENT_HAS_LINK_WITH_N_APPOINTMENTS, qtdAppointments);
    }
}
