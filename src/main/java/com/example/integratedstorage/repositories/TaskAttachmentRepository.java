package com.example.integratedstorage.repositories;


import com.example.integratedstorage.entities.TaskAttachment;
import com.example.integratedstorage.entities.TasksV3;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment,Integer> {
    public TaskAttachment findFirstByNameAndTask(String name, TasksV3 tasks);
}
