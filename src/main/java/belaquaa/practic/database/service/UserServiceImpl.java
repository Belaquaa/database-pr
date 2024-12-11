package belaquaa.practic.database.service;

import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.mapper.PageConverter;
import belaquaa.practic.database.mapper.UserMapper;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserMapper userMapper;

    @Override
    @CachePut(value = "users", key = "#result.externalId")
    @CacheEvict(value = {"usersPage", "usersSearchPage"}, allEntries = true)
    public UserDTO create(UserDTO userDTO) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDTO)));
    }

    @Override
    @CachePut(value = "users", key = "#externalId")
    @CacheEvict(value = {"usersPage", "usersSearchPage"}, allEntries = true)
    public UserDTO updateByExternalId(UUID externalId, UserDTO userDTO) {
        User existing = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        User updatedUser = userMapper.toEntity(userDTO);
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setPatronymic(updatedUser.getPatronymic());
        existing.setPhone(updatedUser.getPhone());
        existing.setAddress(updatedUser.getAddress());
        return userMapper.toDto(userRepository.save(existing));
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
    public UserDTO findByExternalId(UUID externalId) {
        return userMapper.toDto(userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
    }

    @Override
    @Cacheable(value = "usersPage", key = "{#pageable.pageNumber, #pageable.pageSize, #sortField, #sortDir}")
    public PageDto<UserDTO> findAll(Pageable pageable, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageConverter.toPageDto(userRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort))
                .map(userMapper::toDto));
    }

    @Cacheable(value = "usersSearchPage", key = "{#search, #pageable.pageNumber, #pageable.pageSize, #sortField, #sortDir}")
    public PageDto<UserDTO> searchUsers(String search, Pageable pageable, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageConverter.toPageDto(userSearchService
                .searchUsers(search, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort))
                .map(userMapper::toDto));
    }

    @Override
    public List<UserDTO> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }
}