package com.example.integratedstorage.controllers;


import com.example.integratedstorage.dto.RequestRemoveFilesDto;
import com.example.integratedstorage.entities.TaskAttachment;
import com.example.integratedstorage.services.TaskAttachmentImgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "${value.url.cross.origin}")
@RequestMapping("/task-attachment")
public class TaskAttachmentController {
    @Autowired
    private TaskAttachmentImgService service;

    @PutMapping("{taskId}")
    public ResponseEntity<Object> uploadAttachmentTask(@PathVariable Integer taskId, @RequestParam("files")List<MultipartFile> files){
        return ResponseEntity.ok(service.storeImgTask(taskId,files));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getAttachFile(@PathVariable String filename){
        Resource file = service.loadFileAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    @DeleteMapping("")
    @ResponseBody
    public ResponseEntity<Object> deleteFiles(@RequestBody RequestRemoveFilesDto requestRemoveFilesDto){
        List<TaskAttachment> files = service.removeFiles(requestRemoveFilesDto);
        return ResponseEntity.ok(files);
    }


}
