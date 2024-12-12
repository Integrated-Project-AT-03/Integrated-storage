package com.example.integratedstorage.controllers;


import com.example.integratedstorage.dto.RequestRemoveFilesDto;
import com.example.integratedstorage.entities.TaskAttachment;
import com.example.integratedstorage.services.TaskAttachmentImgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

        // กำหนด Content-Type ตามชนิดของไฟล์จากชื่อไฟล์
        String contentType;
        if (filename.toLowerCase().endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
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
