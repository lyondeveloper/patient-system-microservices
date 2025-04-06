package com.pm.accountservice.model;

import com.pm.accountservice.util.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="users")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRoles role = UserRoles.ROLE_USER;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="is_active")
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // muchos usuarios pueden pertenecer a multiples tenants
    // creando relacion ManyToMany con una tabla de por medio para
    // almacenar puros IDs
    @ManyToMany(fetch = FetchType.LAZY)
    // hacemos que user sea la tabla que tenga la clave primaria
    @JoinTable(
            name="user_tenant",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id")
    )
    // inicializamos un Set para evitar duplicados, un Set siempre guarda datos unicos
    private Set<Tenant> tenants;
}
