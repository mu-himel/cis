package com.aes.erp.scm.dto;

import java.util.List;

import com.aes.erp.scm.dto.remote.CommentAttachmentDto;

import lombok.Data;

@Data
public class NoteDto {
    private List<CommentAttachmentDto> attachments;
    private String note;
}
