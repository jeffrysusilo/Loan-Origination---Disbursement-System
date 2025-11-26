package com.los.customer.service;

import com.los.customer.dto.CustomerRequest;
import com.los.customer.dto.CustomerResponse;
import com.los.customer.entity.Customer;
import com.los.customer.event.CustomerEvent;
import com.los.customer.exception.CustomerAlreadyExistsException;
import com.los.customer.exception.CustomerNotFoundException;
import com.los.customer.mapper.CustomerMapper;
import com.los.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;
    private final OcrService ocrService;

    private static final String CUSTOMER_TOPIC = "customer-events";

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer with NIK: {}", request.getNik());

        // Validate NIK uniqueness
        if (customerRepository.existsByNik(request.getNik())) {
            throw new CustomerAlreadyExistsException("Customer with NIK " + request.getNik() + " already exists");
        }

        // Validate email uniqueness
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email " + request.getEmail() + " already exists");
        }

        // Create customer entity
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        // Publish event
        publishEvent(savedCustomer, "CUSTOMER_CREATED");

        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer by ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByNik(String nik) {
        log.info("Fetching customer by NIK: {}", nik);
        Customer customer = customerRepository.findByNik(nik)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with NIK: " + nik));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        // Update customer data
        customerMapper.updateEntityFromRequest(request, customer);
        Customer updatedCustomer = customerRepository.save(customer);

        // Publish event
        publishEvent(updatedCustomer, "CUSTOMER_UPDATED");

        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        return customerMapper.toResponse(updatedCustomer);
    }

    @Transactional
    public void verifyCustomer(Long id) {
        log.info("Verifying customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setIsVerified(true);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customerRepository.save(customer);

        // Publish event
        publishEvent(customer, "CUSTOMER_VERIFIED");

        log.info("Customer verified successfully with ID: {}", id);
    }

    @Transactional
    public void blacklistCustomer(Long id, String reason) {
        log.info("Blacklisting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setStatus(Customer.CustomerStatus.BLACKLISTED);
        customerRepository.save(customer);

        // Publish event
        CustomerEvent event = CustomerEvent.builder()
                .customerId(customer.getId())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .eventType("CUSTOMER_BLACKLISTED")
                .metadata("Reason: " + reason)
                .build();

        kafkaTemplate.send(CUSTOMER_TOPIC, event);

        log.info("Customer blacklisted successfully with ID: {}", id);
    }

    private void publishEvent(Customer customer, String eventType) {
        CustomerEvent event = CustomerEvent.builder()
                .customerId(customer.getId())
                .nik(customer.getNik())
                .email(customer.getEmail())
                .eventType(eventType)
                .build();

        kafkaTemplate.send(CUSTOMER_TOPIC, event);
        log.info("Published event: {} for customer ID: {}", eventType, customer.getId());
    }
}
