package org.kutaka.adminapi.repository;

import org.springframework.stereotype.Repository;
import org.kutaka.adminapi.model.Essay;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface EssayRepository extends MongoRepository<Essay, String> {
}
