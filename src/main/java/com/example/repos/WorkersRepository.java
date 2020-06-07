package com.example.repos;

import com.example.domain.Worker;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkersRepository extends CrudRepository<Worker, Integer>, JpaSpecificationExecutor<Worker> {
    Worker findById(int id);
    List<Worker> findByDegreeId(int degreeId);
    List<Worker> findByPositionId(int positionId);
    List<Worker> findByPositionIdAndDegreeId(int positionId,int degreeId);
    List<Worker> findByLastName(String lastName);
    List<Worker> findByParentId(Integer parentId);
    void deleteByDegreeId(int id);
}