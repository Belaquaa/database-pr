package belaquaa.practic.database.service;

import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSearchService userSearchService;

    @Override
    @CachePut(value = "users", key = "#result.externalId")
    @CacheEvict(value = {"usersPage", "usersSearchPage"}, allEntries = true)
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @CachePut(value = "users", key = "#externalId")
    @CacheEvict(value = {"usersPage", "usersSearchPage"}, allEntries = true)
    public User updateByExternalId(UUID externalId, User user) {
        User existing = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setPatronymic(user.getPatronymic());
        existing.setPhone(user.getPhone());
        existing.setAddress(user.getAddress());
        return userRepository.save(existing);
    }

    @Override
    @CacheEvict(value = {"users", "usersPage", "usersSearchPage"}, key = "#externalId", allEntries = true)
    public void deleteByExternalId(UUID externalId) {
        User existing = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        userRepository.delete(existing);
    }

    @Override
    @Cacheable(value = "users", key = "#externalId")
    public User findByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    @Cacheable(value = "usersPage", key = "{#pageable.pageNumber, #pageable.pageSize}")
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "usersSearchPage", key = "{#search, #pageable.pageNumber, #pageable.pageSize}")
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userSearchService.searchUsers(search, pageable);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}