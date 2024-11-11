package com.example.integratedstorage.repositories;


import com.example.integratedstorage.entities.TaskAttachment;
import com.example.integratedstorage.entities.TasksV3;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment,String> {
    public TaskAttachment findFirstByNameAndTypeAndTask(String name,String type, TasksV3 tasks);
}
