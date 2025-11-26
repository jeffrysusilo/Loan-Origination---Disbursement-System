package com.los.customer.repository;

import com.los.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNik(String nik);
    Optional<Customer> findByEmail(String email);
    boolean existsByNik(String nik);
    boolean existsByEmail(String email);
}
