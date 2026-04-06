package com.motily.region;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class Region extends PanacheEntityBase {
    @Id
    public Long id;

    @Column(name = "code", unique = true, nullable = false, length = 10)
    public String code;

    @Column(name = "name", nullable = false, length = 50)
    public String name;

    @Column(name = "population_weight", nullable = false)
    public double populationWeight;

    @Column(name = "level", nullable = false)
    public int level; // 1=直辖市, 2=省, 3=自治区, 4=特别行政区

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"code\": \"" + (code != null ? code : "") + "\"" +
            ", \"name\": \"" + (name != null ? name : "") + "\"" +
            ", \"populationWeight\": " + populationWeight +
            ", \"level\": " + level +
            ", \"createdAt\": \"" + createdAt + "\"" +
            ", \"updatedAt\": \"" + updatedAt + "\"" +
            "}";
    }
}
