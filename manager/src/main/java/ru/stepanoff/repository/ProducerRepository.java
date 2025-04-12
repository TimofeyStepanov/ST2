package ru.stepanoff.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.stepanoff.document.ProducerRule;

import java.util.Optional;

@Repository
public interface ProducerRepository extends MongoRepository<ProducerRule, String> {
    Optional<ProducerRule> findTopByOrderByCreatedAtDesc();
}
