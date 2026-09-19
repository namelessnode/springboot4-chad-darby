package com.example.cruddemo.dao;

import com.example.cruddemo.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class StudentDAOImpl  implements StudentDAO {

    private EntityManager entityManager;

    @Autowired
    public StudentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Student student) {
        entityManager.persist(student);
    }

    @Override
    public Student findById(int id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    public List<Student> findAllHql() {
        TypedQuery<Student> query = entityManager.createQuery("From Student", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastNameAscHql() {
        TypedQuery<Student> query = entityManager.createQuery("From Student order by lastName asc", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastNameDescHql() {
        TypedQuery<Student> query = entityManager.createQuery("From Student order by lastName desc", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllJpql() {
        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastNameAscJpql() {
        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s order by s.lastName asc", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findAllByLastNameDescJpql() {
        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s order by s.lastName desc", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findByLastNameWithoutSettingParamHql(String lastName) {
        TypedQuery<Student> query = entityManager.createQuery("From Student where lastName = '"+lastName+"'", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findByLastNameWithoutSettingParamJpql(String lastName) {
        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s where lastName = '"+lastName+"'", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findByLastNameWithSettingParamHql(String lastName) {
        TypedQuery<Student> query = entityManager.createQuery("From Student where lastName=:lastNamePlaceholder", Student.class);
        query.setParameter("lastNamePlaceholder", lastName);
        return query.getResultList();
    }

    @Override
    public List<Student> findByLastNameWithSettingParamJpql(String lastName) {
        TypedQuery<Student> query = entityManager.createQuery("Select s From Student s where s.lastName=:lastNamePlaceholder", Student.class);
        query.setParameter("lastNamePlaceholder", lastName);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void update(Student student) {
        Student retrievedStudent = findById(student.getId());
        if(retrievedStudent != null) {
            entityManager.merge(student);
        }
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Student retrievedStudent = findById(id);
        if(retrievedStudent != null) {
            entityManager.remove(retrievedStudent);
        }
    }

    @Override
    @Transactional
    public int deleteAll() {
        return entityManager.createQuery("delete from  Student").executeUpdate();
    }
}
