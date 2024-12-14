package belaquaa.practic.database.repository;

import belaquaa.practic.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> searchUsersWithRegex(String search, Pageable pageable);
}