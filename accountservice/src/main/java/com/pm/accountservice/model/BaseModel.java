package com.pm.accountservice.model;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
// entity listener para que escuche eventos del ciclo de vida
// basicamente detecta que hay campos de auditoria y asigna valores automaticmaente
// es el conector entre las anotaciones @CreatedDate y @LastModifiedDate
// y el EnableAuditoring JPAConfig
public class BaseModel {
    @NotNull
    @CreatedDate
    @Column("created_at")
    private Instant createdDate;

    @NotNull
    @LastModifiedDate
    @Column("updated_at")
    private Instant lastModifiedDate;
}
