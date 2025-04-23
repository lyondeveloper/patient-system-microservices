package com.pm.accountservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "tenants")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tenant extends BaseModel {
    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @Column("phone_number")
    private String phoneNumber;

    private Long addressId;
}
