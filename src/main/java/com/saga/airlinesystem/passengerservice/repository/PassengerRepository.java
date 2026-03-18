package com.saga.airlinesystem.passengerservice.repository;

import com.saga.airlinesystem.passengerservice.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    Optional<Passenger> findByEmail(String email);

    @Modifying
    @Query("""
        update Passenger p
        set p.miles = p.miles + :miles
        where p.id = :id
    """)
    void addMiles(@Param("id") UUID id, @Param("miles") int miles);
}
