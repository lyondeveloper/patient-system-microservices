package com.pm.accountservice.model;

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
    private String role;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    // muchos usuarios pueden pertenecer a multiples tenants
    // creando relacion ManyToMany con una tabla de por medio para
    // almacenar puros IDs
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // hacemos que user sea la tabla que tenga la clave primaria
    @JoinTable(
            name="user_tenant",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id")
    )
    // inicializamos un Set para evitar duplicados, un Set siempre guarda datos unicos
    private Set<Tenant> tenants;
}
