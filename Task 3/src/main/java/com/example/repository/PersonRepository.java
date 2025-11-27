package com.example.repository;

import com.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Поиск по фамилии
    List<Person> findByLastName(String lastName);

    // Поиск по фамилии (игнорируя регистр)
    List<Person> findByLastNameIgnoreCase(String lastName);

    // Поиск по имени и фамилии
    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    // Поиск по email
    List<Person> findByEmail(String email);

    // Поиск людей старше указанного возраста
    List<Person> findByAgeGreaterThan(Integer age);

    // Кастомный запрос для поиска по части фамилии
    @Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName%")
    List<Person> findByLastNameContaining(@Param("lastName") String lastName);
}