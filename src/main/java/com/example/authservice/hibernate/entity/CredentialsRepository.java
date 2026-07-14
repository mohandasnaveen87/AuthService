package com.example.authservice.hibernate.entity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
    // By extending JpaRepository<Entity, ID_Type>, Spring automatically implements
    // methods like save(), findById(), findAll(), deleteById(), etc.
	Optional<Credentials> findByUsername(String username);
	
}