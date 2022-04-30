package pl.aw84.imagelib.imageapi.repository;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

import pl.aw84.imagelib.imageapi.entity.Image;

public interface ImageRepository extends PagingAndSortingRepository<Image, UUID> {

}
