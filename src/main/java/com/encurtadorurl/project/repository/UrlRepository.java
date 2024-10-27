package com.encurtadorurl.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.encurtadorurl.project.entities.UrlEntity;

public interface UrlRepository extends MongoRepository<UrlEntity, String> {
    
}
