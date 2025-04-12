package com.pm.accountservice.model;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
// entity listener para que escuche eventos del ciclo de vida
// basicamente detecta que hay campos de auditoria y asigna valores automaticmaente
// es el conector entre las anotaciones @CreatedDate y @LastModifiedDate
// y el EnableAuditoring JPAConfig
@EntityListeners(AuditingEntityListener.class)
public class BaseModel {
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant lastModifiedDate;
}
