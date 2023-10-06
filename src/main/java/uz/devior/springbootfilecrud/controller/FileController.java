package uz.devior.springbootfilecrud.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.UriEncoder;
import uz.devior.springbootfilecrud.entity.FileEntity;
import uz.devior.springbootfilecrud.service.FileService;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    @Value("${upload.server.folder}")
    private String staticUploadFolder;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile multipartFile){
        FileEntity file = fileService.save(multipartFile);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/file-preview/{hashId}")
    public ResponseEntity<?> preview(@PathVariable String hashId) throws MalformedURLException {
        FileEntity file = fileService.findByHashId(hashId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=\""+ UriEncoder.encode(file.getOriginalName()))
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getFileSize())
                .body(new FileUrlResource(String.format("%s%s",this.staticUploadFolder,file.getUploadFolder())));
    }

    @GetMapping("/download/{hashId}")
    public ResponseEntity<?> download(@PathVariable String hashId) throws MalformedURLException {
        FileEntity file = fileService.findByHashId(hashId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\""+ UriEncoder.encode(file.getOriginalName()))
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getFileSize())
                .body(new FileUrlResource(String.format("%s%s",this.staticUploadFolder,file.getUploadFolder())));
    }

    @DeleteMapping("/delete/{hashId}")
    public ResponseEntity<?> delete(@PathVariable String hashId){
        fileService.delete(hashId);
        return ResponseEntity.ok("File is deleted with this ID: "+hashId);
    }
}
