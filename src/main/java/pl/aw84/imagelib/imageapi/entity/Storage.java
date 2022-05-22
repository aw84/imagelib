package pl.aw84.imagelib.imageapi.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@TypeDef(name = "image_quality", typeClass = ImageQualityType.class)
@Table(name = "storage")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "storageId")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Storage {
    @Id
    @Column(name = "storage_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID storageId;

    @ManyToOne()
    @JoinColumn(name = "image_id", nullable = false)
    @ToString.Exclude
    private Image image;

    private String protocol;
    private String host;

    @Column(name = "relative_path")
    private String relativePath;

    @Enumerated(EnumType.STRING)
    @Type(type = "image_quality")
    private ImageQualityEnum quality;

    private String hash;

}
