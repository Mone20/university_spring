package com.example.domain;

import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Objects;
@Lazy
@Entity
public class Worker implements EntityInterface {
    @Id
    private Integer id;
    private String firstName;
    private String birthDate;
    private String lastName;
    private String middleName;
    private Integer salery;


    public Integer getParentId() {
        return parentId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }


    public Worker(String firstName, String birthDate, int id, String lastName, String middleName, int parentId,int salery) {
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.id = id;
        this.lastName = lastName;
        this.middleName = middleName;
        this.parentId = parentId;
        this.salery=salery;
    }
    private String pId;
    private String dId;
    @ManyToOne (optional=false,fetch = FetchType.LAZY)
    private Position position;

    @ManyToOne (optional=false,fetch = FetchType.LAZY)
    private Degree degree;

    private Integer parentId;
    public Worker(int id)
    {
        this.id=id;

    }
    public Worker()
    {

    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Integer getId() {
        return id;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Worker worker = (Worker) o;
        System.out.println("ID:"+ (id == worker.id));
        System.out.println("Salery"+( salery==worker.salery));
        System.out.println("FirstName"+( Objects.equals(firstName, worker.firstName)));
        System.out.println("LastName"+   Objects.equals(lastName, worker.lastName));
        System.out.println("MiddleName"+   Objects.equals(middleName, worker.middleName));
        System.out.println("BirthDate"+   Objects.equals(birthDate, worker.birthDate));
        System.out.println("ParentId"+( parentId == worker.parentId));
        System.out.println("PosId"+Objects.equals(pId, worker.pId));
        System.out.println("DegId"+   Objects.equals(dId, worker.dId));
        return  id == worker.id &&
                parentId == worker.parentId &&
                salery==worker.salery&&
                Objects.equals(firstName, worker.firstName) &&
                Objects.equals(birthDate, worker.birthDate) &&
                Objects.equals(lastName, worker.lastName) &&
                Objects.equals(middleName, worker.middleName)&&
                Objects.equals(pId, worker.pId)&&
                Objects.equals(dId, worker.dId);
    }


    public String getInf() {
        return this.getId()+"| "+this.getLastName()+" |"+"| "+this.getFirstName()+" |"+this.getMiddleName()+"|"+this.getBirthDate()+"|"+this.getSalery();
    }
    public boolean isEmpty()
    {
        if (this.firstName != null && this.firstName.length() > 0
                && this.lastName != null && this.lastName.length() > 0 && this.middleName != null && this.middleName.length() > 0
                && this.birthDate != null && this.birthDate.length() > 0&&this.salery!=null&&this.dId!=null&&this.dId.length()>0&&
        this.pId.length()>0&&this.pId!=null)
            return false;

        return true;
    }
    public void deleteBoss()
    {
        this.parentId=null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, birthDate, id, lastName, middleName,  parentId);
    }

    public Integer getSalery() {
        return salery;
    }

    public void setSalery(Integer salery) {
        this.salery = salery;
    }
}
