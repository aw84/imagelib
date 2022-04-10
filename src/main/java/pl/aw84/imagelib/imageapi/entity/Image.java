package pl.aw84.imagelib.imageapi.entity;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "image")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "imageId")
public class Image {
    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID imageId;

    @Column(name = "cname")
    private String name;

    @OneToMany(mappedBy = "image")
    private Set<Storage> storages;

    public Image() {
    }

    public Image(UUID imageId, Set<Storage> storage, String name) {
        this.imageId = imageId;
        this.storages = storage;
        this.name = name;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Storage> getStorage() {
        return storages;
    }

    public void setStorage(Set<Storage> storage) {
        this.storages = storage;
    }

    @Override
    public String toString() {
        return "Image [imageId=" + imageId +
                ", name=" + name +
                ", storages=" + storages +
                "]";
    }
}
