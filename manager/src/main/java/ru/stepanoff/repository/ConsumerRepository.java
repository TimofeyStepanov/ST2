package ru.stepanoff.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.stepanoff.document.ConsumerRule;

import java.util.Optional;


public interface ConsumerRepository extends MongoRepository<ConsumerRule, String> {
    Optional<ConsumerRule> findTopByOrderByCreatedAtDesc();
}
