package com.meiorganizadinho.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service")
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private int duration;

    @ManyToMany(mappedBy = "services")
    private List<Appointment> appointments = new ArrayList<>();

    public Services(){}

    public Services(String name, double value, int duration) {
        this.name = name;
        this.value = value;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
