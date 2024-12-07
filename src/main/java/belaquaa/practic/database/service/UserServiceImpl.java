package belaquaa.practic.database.service;

import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
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
    public void deleteByExternalId(UUID externalId) {
        User existing = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        userRepository.delete(existing);
    }

    @Override
    public User findByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPatronymicContainingIgnoreCase(
                search, search, search, pageable);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}