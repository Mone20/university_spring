package com.example.domain;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
@Lazy
@Entity
public class Position  {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String position;
    @OneToMany (mappedBy="position", fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    List<Worker> workers;
    public Position(String position) {
        this.position = position;
    }
    public Position(Integer id) {
        this.id = id;
    }
    public Position() {

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public Integer getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }
}
