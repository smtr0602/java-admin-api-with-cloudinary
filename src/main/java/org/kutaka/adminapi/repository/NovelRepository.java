package org.kutaka.adminapi.repository;

import org.springframework.stereotype.Repository;
import org.kutaka.adminapi.model.Novel;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface NovelRepository extends MongoRepository<Novel, String> {
}
