package uz.devior.springbootfilecrud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.devior.springbootfilecrud.entity.enumeration.FileStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;

    private String extension;

    private Long fileSize;

    private String mimeType;// contentType

    private String hashId;

    private FileStatus fileStatus;

    private String uploadFolder;
}
