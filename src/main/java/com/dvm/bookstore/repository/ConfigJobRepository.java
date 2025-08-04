package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.ConfigJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigJobRepository extends JpaRepository<ConfigJob, Integer> {
    List<ConfigJob> findAllByStatus(Integer status);
}
