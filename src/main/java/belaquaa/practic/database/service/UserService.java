package belaquaa.practic.database.service;

import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.dto.UserDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface UserService {
    UserDTO create(UserDTO userDTO);

    UserDTO updateByExternalId(UUID externalId, UserDTO userDTO);

    void deleteByExternalId(UUID externalId);

    UserDTO findByExternalId(UUID externalId);

    PageDto<UserDTO> findAll(Pageable pageable, String sortField, String sortDir);

    PageDto<UserDTO> searchUsers(String search, Pageable pageable, String sortField, String sortDir);

    List<UserDTO> findAll();
}