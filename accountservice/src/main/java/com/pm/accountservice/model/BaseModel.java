package com.pm.accountservice.model;
import org.springframework.data.annotation.*;

import java.time.Instant;

public class BaseModel {
    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private String lastModifiedDate;

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
