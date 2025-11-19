CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE service (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    value DOUBLE PRECISION NOT NULL,
    duration INTEGER NOT NULL
);

CREATE TABLE appointment (
                             id BIGSERIAL PRIMARY KEY,
                             id_client BIGINT NOT NULL,
                             date DATE NOT NULL,
                             start_time TIME NOT NULL,
                             end_time TIME NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointment_services (
                                      id_appointment BIGINT NOT NULL,
                                      service_id BIGINT NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (id_appointment, service_id)
);

ALTER TABLE appointment
    ADD CONSTRAINT fk_appointment_client
        FOREIGN KEY (id_client) REFERENCES client(id);

ALTER TABLE appointment_services
    ADD CONSTRAINT fk_appointment_services_appointment
        FOREIGN KEY (id_appointment) REFERENCES appointment(id);

ALTER TABLE appointment_services
    ADD CONSTRAINT fk_appointment_services_service
        FOREIGN KEY (service_id) REFERENCES service(id);

ALTER TABLE appointment
    ADD CONSTRAINT chk_appointment_times
        CHECK (start_time < end_time);