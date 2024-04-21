package com.example.whynotpc.models.img;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

/**
 * Entity class representing an image stored in the database.
 */
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
    /**
     * Unique identifier for the image.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the image.
     */
    private String name;

    /**
     * The type of the image.
     */
    private String type;

    /**
     * The binary data of the image.
     */
    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] imageData;
}
