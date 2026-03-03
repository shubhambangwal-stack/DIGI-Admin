package com.vunum.SocietyAdmin.DTO;

import jakarta.mail.Multipart;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Getter
@Setter
@Builder
public class ResponseDTO {
    private List<MultipartFile> files;
    private MultipartFile file;
}
