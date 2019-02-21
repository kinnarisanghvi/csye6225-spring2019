package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.repository.AttachmentRepository;
import com.csye6225.spring2019.repository.NoteRepository;
import com.csye6225.spring2019.repository.UserRepository;
import com.csye6225.spring2019.utils.AmazonClient;
import com.csye6225.spring2019.utils.UserCheck;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
public class AttachmentController {


    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository uRepository;
    @Autowired
    AttachmentRepository attachmentRepository;


    UserCheck uCheck = new UserCheck();
    String auth_user = null;
    String[] auth_user_1 = new String[3];

    private AmazonClient amazonClient;


    @GetMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> getAllAttachments(@PathVariable(value = "idNotes") String idNotes,HttpServletRequest request, HttpServletResponse response) throws JSONException {

        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {

            auth_user_1 = auth_user.split(",");
            if (auth_user_1[0].equalsIgnoreCase("Success")) {
                Note note = noteRepository.findById(idNotes).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", idNotes));

                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();
                //if (note.get().getUser().getId() == Long.valueOf(auth_user_1[1])) {
                for (Attachment att : note.getAttachmentList()) {
                    entity.put("Id", att.getAttachmentId());
                    entity.put("Url", att.getUrl());
                    entities.add(entity);
                }
                //  entity.put("attachments",note.orElseThrow(RuntimeException::new).getAttachment());

                return new ResponseEntity<Object>(entities.toString(), HttpStatus.FOUND);


            }

        }
        return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/note/{idNotes}/attachments")
    public ResponseEntity<Object> newAttachment(@PathVariable(value="idNotes") String idNotes ,@RequestPart(value="file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws JSONException {
        auth_user = uCheck.loginUser(request, response, uRepository);
        if (auth_user == "0") {
            return new ResponseEntity<Object>("{\"message\": \"Invalid Login\"}", HttpStatus.NOT_ACCEPTABLE);
        } else if (auth_user == "1") {
            return new ResponseEntity<Object>("{\"message\": \"Unauthorized User\"}", HttpStatus.FORBIDDEN);
        } else if (auth_user == "2") {
            return new ResponseEntity<Object>("{\"message\": \"Incorrect Authorization Headers\"}", HttpStatus.UNAUTHORIZED);
        } else {
            String url = amazonClient.uploadFile(file);
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();

            Attachment attachment = new Attachment();
            attachment.setAttachmentId(randomUUIDString);
            attachment.setUrl(url);
            //attachment.getNote().setNoteId(idNotes);

            attachmentRepository.save(attachment);

            List<JSONObject> entities = new ArrayList<JSONObject>();
            JSONObject entity = new JSONObject();

            entity.put("id",attachment.getAttachmentId());
            entity.put("url",attachment.getUrl());
            entities.add(entity);

            return new ResponseEntity<Object>(entities.toString(),HttpStatus.OK);
        }


    }

    @DeleteMapping("/note/{idNotes}/attachments/{idAttachments}")
    public void deleteAttachment(@PathVariable(value = "idNotes") String idNotes, @PathVariable (value="idAttachments") String idAttachments){

        Attachment attachment = attachmentRepository.getOne(idAttachments);
        String url = attachment.getUrl();
        amazonClient.deleteFileFromS3Bucket(url);
        attachmentRepository.delete(attachment);

    }
}
