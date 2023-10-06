package uz.devior.springbootfilecrud.service;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.devior.springbootfilecrud.entity.FileEntity;
import uz.devior.springbootfilecrud.entity.enumeration.FileStatus;
import uz.devior.springbootfilecrud.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class FileService {

    private final FileRepository fileRepository;
    @Value("${upload.server.folder}")
    private String staticUploadFolder;
    private Hashids hashids;

    public FileService(FileRepository fileRepository){
        this.fileRepository = fileRepository;
        this.hashids = new Hashids(getClass().getName(), 6);

    }

    public FileEntity save(MultipartFile multipartFile) {
//        Get File Details from multipart file
        FileEntity file = new FileEntity();
        file.setOriginalName(multipartFile.getOriginalFilename());
        file.setFileSize(multipartFile.getSize());
        file.setMimeType(multipartFile.getContentType());
        file.setExtension(getFileExtension(file.getOriginalName()));
        file.setFileStatus(FileStatus.DRAFT);
        file = fileRepository.save(file);

//        Prepare folder to upload file year/month/day format
        LocalDate date = LocalDate.now();
        String staticPath = String.format("%s/uploaded_files/%d/%d/%d",
                this.staticUploadFolder,date.getYear(),date.getMonthValue(),date.getDayOfMonth());
        File folderPath = new File(staticPath);
        if(!folderPath.exists() && folderPath.mkdirs()){
            System.out.println("folder is created");
        }

//        Setting hashId from id
        file.setHashId(this.hashids.encode(file.getId()));
//        Setting uploadFolder path
        String dynamicPath = String.format("/uploaded_files/%d/%d/%d/%s.%s",
                date.getYear(),date.getMonthValue(),date.getDayOfMonth(),file.getHashId(),file.getExtension());
        file.setUploadFolder(dynamicPath);
//        Getting binaryFile and locating to upload folder
        folderPath = folderPath.getAbsoluteFile();
        File fileBinary = new File(folderPath,String.format("%s.%s",file.getHashId(),file.getExtension()));
        try {
            multipartFile.transferTo(fileBinary);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = fileRepository.save(file);
        return file;
    }

    public FileEntity findByHashId(String hashId){
        return fileRepository.findByHashId(hashId);
    }

    public void delete(String hashId) {
        FileEntity fileEntity = fileRepository.findByHashId(hashId);
        File file = new File(String.format("%s%s", this.staticUploadFolder, fileEntity.getUploadFolder()));
        if(file.delete()){
            fileRepository.delete(fileEntity);
        }
    }

    private String getFileExtension(String fileName) {
        String extension = null;
        if (fileName != null && !fileName.isEmpty()) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex <= fileName.length() - 2) {
                extension = fileName.substring(dotIndex+1);
            }
        }
        return extension;
    }
}
