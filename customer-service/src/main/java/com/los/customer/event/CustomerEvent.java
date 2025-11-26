package com.los.customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEvent {
    private Long customerId;
    private String nik;
    private String email;
    private String eventType;
    private String metadata;
}
