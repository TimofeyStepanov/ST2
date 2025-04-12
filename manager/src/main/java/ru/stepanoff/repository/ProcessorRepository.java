package ru.stepanoff.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.stepanoff.document.ProcessorRule;

import java.util.Optional;

@Repository
public interface ProcessorRepository extends MongoRepository<ProcessorRule, String> {
    Optional<ProcessorRule> findTopByOrderByCreatedAtDesc();
}
