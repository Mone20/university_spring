package com.example.domain;

import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
@Lazy
@Entity
public class Degree implements EntityInterface{
    @Id
    private Integer id;
    private String degree;
    @OneToMany(mappedBy="degree", fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    List<Worker> workers;
    public Degree(String degree) {
        this.degree = degree;
    }
    public Degree()
    {

    }
    public Degree(int id,String degree)
    {
this.degree=degree;
this.id=id;
    }
    public Degree(Integer id)
    {
        this.id=id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Degree degree1 = (Degree) o;
        return degree.equals(degree1.degree);
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(degree);
    }

    public Integer getId() {
        return id;
    }

    public String getDegree() {
        return degree;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
