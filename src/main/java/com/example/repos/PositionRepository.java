package com.example.repos;

import com.example.domain.Position;
import org.springframework.data.repository.CrudRepository;
public interface PositionRepository extends CrudRepository<Position, Integer> {
    Position findById(int id);

}