package com.edu.uba.support.repository;

import com.edu.uba.support.model.Resource;
import com.edu.uba.support.model.Task;
import com.edu.uba.support.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
}

