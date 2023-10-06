package uz.devior.springbootfilecrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.devior.springbootfilecrud.entity.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    FileEntity findByHashId(String hashId);
}
