package com.example.integratedstorage.services;

import com.example.integratedstorage.dto.RequestRemoveFilesDto;
import com.example.integratedstorage.entities.TaskAttachment;
import com.example.integratedstorage.entities.TasksV3;
import com.example.integratedstorage.exceptions.ConflictException;
import com.example.integratedstorage.exceptions.ItemNotFoundException;
import com.example.integratedstorage.repositories.TaskAttachmentRepository;
import com.example.integratedstorage.repositories.TaskRepositoryV3;
import com.example.integratedstorage.utils.CustomNanoId;

import java.util.concurrent.CompletableFuture;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TaskAttachmentImgService {
    @Autowired
    private TaskAttachmentRepository repository ;
    @Autowired
    private TaskRepositoryV3 taskRepository ;

    private  Path findStorageLocation;
    @Value("${value.upload.dir}")
    private String propertyUploadDir;

    @PostConstruct
    public void init() {
        this.findStorageLocation = Paths.get(propertyUploadDir).toAbsolutePath().normalize();

        try {
            if (!Files.exists(this.findStorageLocation)) {
                Files.createDirectories(this.findStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.findStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ItemNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File operation error" + fileName, e);
        }
    }

    @Transactional
    public List<TaskAttachment> removeFiles(RequestRemoveFilesDto requestRemoveFilesDto) {
        List<TaskAttachment> taskAttachments = new ArrayList<>();
        for(String fileId : requestRemoveFilesDto.getFilesId()) {
            TaskAttachment taskAttachment = repository.findById(fileId).orElseThrow(() -> new ItemNotFoundException("Not found file id : " + fileId));
            taskAttachments.add(taskAttachment);
            String filename = taskAttachment.getId()+'-'+taskAttachment.getTask().getIdTask()+'-'+taskAttachment.getName()+'.'+taskAttachment.getType();
            try {
                Path filePath = this.findStorageLocation.resolve(filename).normalize();
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                } else {
                    throw new RuntimeException("File not found " + filename);
                }
            } catch (IOException e) {
                throw new RuntimeException("File operlation error: " + filename, e);
            }
            repository.delete(taskAttachment);
        }
        return taskAttachments;
    }



    public List<TaskAttachment> storeImgTask(Integer taskId, List<MultipartFile> files) {
        TasksV3 task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task " + taskId + " does not exist !!!!"));

        List<TaskAttachment> savedAttachments = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<Void> saveFileFuture = CompletableFuture.runAsync(() -> {
                try {
                    String nameOrigin = file.getOriginalFilename();
                    String fileName = nameOrigin.substring(0,nameOrigin.lastIndexOf("."));
                    if(fileName.length() > 100) fileName = fileName.substring(0,100);
                    TaskAttachment taskAttachment = repository.findFirstByNameAndTask(fileName, task);
                    if (taskAttachment != null) throw new ConflictException("file name must be unique within the task");
                    String id = CustomNanoId.generate(16);
                    TaskAttachment newTaskAttachment = new TaskAttachment();
                    newTaskAttachment.setId(id);
                    newTaskAttachment.setTask(task);
                    newTaskAttachment.setName(fileName);
                    newTaskAttachment.setType(nameOrigin.substring(nameOrigin.lastIndexOf(".")+1));
                    TaskAttachment savedAttachment = repository.save(newTaskAttachment);


                    synchronized (savedAttachments) {
                        savedAttachments.add(savedAttachment);
                    }

                    if (file.getOriginalFilename().contains("..")) {
                        throw new RuntimeException("Sorry! Filename contains invalid path sequence" + file.getOriginalFilename());

                    }
                    String newFileName = id + '-' + taskId + '-' + fileName + nameOrigin.substring(nameOrigin.lastIndexOf("."));

                    Path targetLocation = this.findStorageLocation.resolve(newFileName);


                    if (!Files.exists(targetLocation.getParent())) {
                        Files.createDirectories(targetLocation.getParent());
                    }
                    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again", ex);
                }
            });
            try {
                saveFileFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("File saving interrupted or failed.", e);
            }
        }
        return savedAttachments;
    }


}
