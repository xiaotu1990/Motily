package com.motily.society;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Marriage extends PanacheEntityBase {
    @Id
    public Long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

    @Column(name = "husband_id", nullable = false)
    public Long husbandId;

    @Column(name = "wife_id", nullable = false)
    public Long wifeId;

    @Column(name = "wedding_year", nullable = false)
    public int weddingYear;

    @Column(name = "wedding_week", nullable = false)
    public int weddingWeek;

    @Column(name = "status", nullable = false, length = 20)
    public String status;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"husbandId\": " + husbandId +
            ", \"wifeId\": " + wifeId +
            ", \"weddingYear\": " + weddingYear +
            ", \"weddingWeek\": " + weddingWeek +
            ", \"status\": \"" + (status != null ? status : "") + "\"" +
            ", \"createdAt\": \"" + createdAt + "\"" +
            ", \"updatedAt\": \"" + updatedAt + "\"" +
            "}";
    }
}
