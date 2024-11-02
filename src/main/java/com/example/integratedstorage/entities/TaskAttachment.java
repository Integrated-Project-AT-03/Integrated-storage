package com.example.integratedstorage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "task_attachment")
public class TaskAttachment {

    @Id
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "type")
    private String type;

    @Basic
    @CreationTimestamp
    @Column(name = "added_on")
    private Timestamp createdOn;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_task", referencedColumnName = "id_task", nullable = false)
    private TasksV3 task;

}
