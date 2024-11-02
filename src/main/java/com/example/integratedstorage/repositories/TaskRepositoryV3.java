package com.example.integratedstorage.repositories;

import com.example.integratedstorage.entities.TasksV3;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepositoryV3 extends JpaRepository<TasksV3,Integer> {

}
