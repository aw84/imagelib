package pl.aw84.imagelib.imageapi.repository;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import pl.aw84.imagelib.imageapi.entity.Storage;

public interface StorageRepository extends CrudRepository<Storage, UUID> {
}
