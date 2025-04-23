package com.pm.accountservice.model;

import com.pm.accountservice.util.UserRoles;
import com.pm.accountservice.util.UserTypes;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name="users")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel implements Persistable<Long> {
    @Id
    private Long id;

    @Transient
    private boolean isNew = true;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Builder.Default
    private UserTypes type = UserTypes.USER_DEFAULT;

    @NotNull
    @Builder.Default
    private UserRoles role = UserRoles.ROLE_USER;

    @Column("tenant_id")
    private Long tenantId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("phone_number")
    private String phoneNumber;

    @Column("is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    @Column("address_id")
    private Long addressId;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
