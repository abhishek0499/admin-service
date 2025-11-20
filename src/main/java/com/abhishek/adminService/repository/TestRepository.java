package com.abhishek.adminService.repository;

import com.abhishek.adminService.model.TestDef;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface TestRepository extends MongoRepository<TestDef, String> {}