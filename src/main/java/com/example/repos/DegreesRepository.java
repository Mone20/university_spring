package com.example.repos;

import com.example.domain.Degree;
import org.springframework.data.repository.CrudRepository;
public interface DegreesRepository extends CrudRepository<Degree, Integer> {
   Degree findById(int id);
   void deleteById(Integer id);
}