package com.csye6225.spring2019.repository;

import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {




}
