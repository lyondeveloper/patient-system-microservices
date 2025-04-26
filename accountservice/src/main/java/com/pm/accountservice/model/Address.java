package com.pm.accountservice.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table(name = "address")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class Address extends BaseModel {
    private String street;
    private String city;
    private String state;
    private String country;
    @Column("zip_code")
    private String zipCode;
}
