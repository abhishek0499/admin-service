package com.abhishek.adminService.repository;

import com.abhishek.adminService.model.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TestRepository extends MongoRepository<Test, String> {
    List<Test> findByAssignedCandidatesContains(String candidateId);
}