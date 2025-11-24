package com.abhishek.adminService.repository;

import com.abhishek.adminService.model.TestDef;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TestRepository extends MongoRepository<TestDef, String> {
    List<TestDef> findByAssignedCandidatesContains(String candidateId);
}