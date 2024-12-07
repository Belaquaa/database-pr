package belaquaa.practic.database.service;

import belaquaa.practic.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface UserService {
    User create(User user);

    User updateByExternalId(UUID externalId, User user);

    void deleteByExternalId(UUID externalId);

    User findByExternalId(UUID externalId);

    Page<User> findAll(Pageable pageable);

    Page<User> searchUsers(String search, Pageable pageable);

    List<User> findAll();
}