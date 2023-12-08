package com.example.whynotpc.models.img;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "images",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)

public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String type;
    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] imageData;

}
