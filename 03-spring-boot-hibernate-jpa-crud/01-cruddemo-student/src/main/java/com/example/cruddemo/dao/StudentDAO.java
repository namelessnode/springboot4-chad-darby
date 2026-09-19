package com.example.cruddemo.dao;

import com.example.cruddemo.entity.Student;

import java.util.List;

public interface StudentDAO {

//    C - Create
    void save(Student student);

//    R - Read
    Student findById(int id);

    List<Student> findAllHql();
    List<Student> findAllByLastNameAscHql();
    List<Student> findAllByLastNameDescHql();

    List<Student> findAllJpql();
    List<Student> findAllByLastNameAscJpql();
    List<Student> findAllByLastNameDescJpql();

    List<Student> findByLastNameWithoutSettingParamHql(String lastName);
    List<Student> findByLastNameWithoutSettingParamJpql(String lastName);

    List<Student> findByLastNameWithSettingParamHql(String lastName);
    List<Student> findByLastNameWithSettingParamJpql(String lastName);

//    U - update
    void update(Student student);

    void deleteById(int id);

    int deleteAll();
}

