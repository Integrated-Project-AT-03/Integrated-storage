package com.example.integratedstorage.controllers;


import com.example.integratedstorage.services.TaskAttachmentImgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
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

    @PostMapping("{taskId}")
    public ResponseEntity<Object> uploadAttachmentTask(@PathVariable Integer taskId, @RequestParam("files")List<MultipartFile> files){
        return ResponseEntity.ok(service.storeImgTask(taskId,files));
    }


    @GetMapping("test")
    public ResponseEntity<Object> test(){
        return ResponseEntity.ok("I'm here");
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename){
        Resource file = service.loadFileAsResource(filename);
        return ResponseEntity.ok(file);
    }



//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ErrorResponse> handlerMethodValidationException(
//            MethodArgumentNotValidException ex,
//            WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(null,HttpStatus.BAD_REQUEST.value(),null,"Validation error. Check 'errors' field for details. statusForCreateOrUpdate",  request.getDescription(false));
//        for (FieldError fieldError : ex.getFieldErrors()) {
//            errorResponse.addValidationError(fieldError.getField(),fieldError.getDefaultMessage());
//        }
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
}
