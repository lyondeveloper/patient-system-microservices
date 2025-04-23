package com.pm.accountservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table(name = "address")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class Address extends BaseModel {
    @Id
    private Long id;

    private String street;

    private String city;

    private String state;

    private String country;

    @NotNull
    @Column("zip_code")
    private String zipCode;
    @NotNull
    @Column("tenant_id")
    private Long tenantId;
}
