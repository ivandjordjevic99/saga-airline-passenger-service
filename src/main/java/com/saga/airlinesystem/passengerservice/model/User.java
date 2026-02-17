package com.saga.airlinesystem.passengerservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean blacklisted;

    private Integer miles;

    public void addMiles(Integer miles) {
        System.out.println("Adding miles: " + miles);
        this.miles += miles;
        System.out.println("Miles added: " + this.miles);
    }
}
