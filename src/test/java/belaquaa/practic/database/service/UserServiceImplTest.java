package belaquaa.practic.database.service;

import belaquaa.practic.database.dto.AddressDTO;
import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.mapper.UserMapper;
import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserDTO sampleUserDTO;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleAddress = Address.builder()
                .id(1L)
                .street("Ленина")
                .house("10")
                .building("A")
                .build();

        sampleUser = User.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .firstName("Иван")
                .lastName("Иванов")
                .patronymic("Иванович")
                .phone("71234567890")
                .address(sampleAddress)
                .build();

        sampleUserDTO = UserDTO.builder()
                .externalId(sampleUser.getExternalId())
                .firstName("Иван")
                .lastName("Иванов")
                .patronymic("Иванович")
                .phone("71234567890")
                .address(AddressDTO.builder()
                        .street("Ленина")
                        .house("10")
                        .building("A")
                        .build())
                .build();
    }

    @Test
    @DisplayName("Создание пользователя")
    void testCreateUser() {
        when(userMapper.toEntity(sampleUserDTO)).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toDto(sampleUser)).thenReturn(sampleUserDTO);

        UserDTO created = userService.create(sampleUserDTO);

        assertNotNull(created);
        assertEquals(sampleUserDTO.getFirstName(), created.getFirstName());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Обновление пользователя по externalId")
    void testUpdateByExternalId() {
        UUID externalId = sampleUser.getExternalId();
        UserDTO updatedUserDTO = UserDTO.builder()
                .firstName("Пётр")
                .lastName("Петров")
                .patronymic("Петрович")
                .phone("71234567890")
                .address(AddressDTO.builder()
                        .street("Пушкина")
                        .house("20")
                        .building("B")
                        .build())
                .build();

        User updatedUser = User.builder()
                .firstName("Пётр")
                .lastName("Петров")
                .patronymic("Петрович")
                .phone("71234567890")
                .address(Address.builder()
                        .street("Пушкина")
                        .house("20")
                        .building("B")
                        .build())
                .build();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toEntity(updatedUserDTO)).thenReturn(updatedUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toDto(sampleUser)).thenReturn(updatedUserDTO);

        UserDTO result = userService.updateByExternalId(externalId, updatedUserDTO);

        assertNotNull(result);
        assertEquals("Пётр", result.getFirstName());
        assertEquals("Пушкина", result.getAddress().getStreet());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Удаление пользователя по externalId")
    void testDeleteByExternalId() {
        UUID externalId = sampleUser.getExternalId();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(sampleUser));
        doNothing().when(userRepository).delete(sampleUser);

        assertDoesNotThrow(() -> userService.deleteByExternalId(externalId));

        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, times(1)).delete(sampleUser);
    }

    @Test
    @DisplayName("Поиск пользователя по externalId")
    void testFindByExternalId() {
        UUID externalId = sampleUser.getExternalId();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleUserDTO);

        UserDTO result = userService.findByExternalId(externalId);

        assertNotNull(result);
        assertEquals(sampleUserDTO.getFirstName(), result.getFirstName());
        verify(userRepository, times(1)).findByExternalId(externalId);
    }

    @Test
    @DisplayName("Получение всех пользователей с пагинацией")
    void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser), pageable, 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toDto(any(User.class))).thenReturn(sampleUserDTO);

        PageDto<UserDTO> result = userService.findAll(pageable, "firstName", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleUserDTO.getFirstName(), result.getContent().get(0).getFirstName());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Поиск пользователей по имени, фамилии или отчеству")
    void testSearchUsers() {
        String search = "Иван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser), pageable, 1);

        when(userSearchService.searchUsers(eq(search), any(Pageable.class))).thenReturn(page);
        when(userMapper.toDto(any(User.class))).thenReturn(sampleUserDTO);

        PageDto<UserDTO> result = userService.searchUsers(search, pageable, "firstName", "asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleUserDTO.getFirstName(), result.getContent().get(0).getFirstName());
        verify(userSearchService, times(1)).searchUsers(eq(search), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение всех пользователей без пагинации")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        when(userMapper.toDtoList(List.of(sampleUser))).thenReturn(List.of(sampleUserDTO));

        List<UserDTO> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(sampleUserDTO.getFirstName(), users.get(0).getFirstName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Обновление пользователя с несуществующим externalId должно выбросить исключение")
    void testUpdateByExternalId_NotFound() {
        UUID externalId = UUID.randomUUID();
        UserDTO updatedUserDTO = UserDTO.builder()
                .firstName("Алексей")
                .lastName("Алексеев")
                .patronymic("Алексеевич")
                .phone("71234567890")
                .address(AddressDTO.builder()
                        .street("Невская")
                        .house("30")
                        .building("C")
                        .build())
                .build();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateByExternalId(externalId, updatedUserDTO));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("Удаление пользователя с несуществующим externalId должно выбросить исключение")
    void testDeleteByExternalId_NotFound() {
        UUID externalId = UUID.randomUUID();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deleteByExternalId(externalId));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Поиск пользователя с несуществующим externalId должен выбросить исключение")
    void testFindByExternalId_NotFound() {
        UUID externalId = UUID.randomUUID();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.findByExternalId(externalId));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByExternalId(externalId);
    }
}