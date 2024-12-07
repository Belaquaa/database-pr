package belaquaa.practic.database.service;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация объекта Address
        sampleAddress = Address.builder()
                .id(1L)
                .street("Ленина")
                .house("10")
                .building("A")
                .build();

        // Инициализация объекта User с использованием sampleAddress
        sampleUser = User.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .firstName("Иван")
                .lastName("Иванов")
                .patronymic("Иванович")
                .phone("+7(123)456-78-90")
                .address(sampleAddress)
                .build();
    }

    @Test
    @DisplayName("Создание пользователя")
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User created = userService.create(sampleUser);

        assertNotNull(created);
        assertEquals(sampleUser.getFirstName(), created.getFirstName());
        assertEquals(sampleAddress.getStreet(), created.getAddress().getStreet());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Обновление пользователя по externalId")
    void testUpdateByExternalId() {
        UUID externalId = sampleUser.getExternalId();
        Address updatedAddress = Address.builder()
                .id(sampleAddress.getId())
                .street("Пушкина")
                .house("20")
                .building("B")
                .build();

        User updatedInfo = User.builder()
                .firstName("Пётр")
                .lastName("Петров")
                .patronymic("Петрович")
                .phone("+7(987)654-32-10")
                .address(updatedAddress)
                .build();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User updated = userService.updateByExternalId(externalId, updatedInfo);

        assertNotNull(updated);
        assertEquals("Пётр", updated.getFirstName());
        assertEquals("Петров", updated.getLastName());
        assertEquals("Петрович", updated.getPatronymic());
        assertEquals("+7(987)654-32-10", updated.getPhone());
        assertEquals("Пушкина", updated.getAddress().getStreet());
        assertEquals("20", updated.getAddress().getHouse());
        assertEquals("B", updated.getAddress().getBuilding());
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

        User found = userService.findByExternalId(externalId);

        assertNotNull(found);
        assertEquals(sampleUser.getExternalId(), found.getExternalId());
        assertEquals(sampleAddress.getStreet(), found.getAddress().getStreet());
        verify(userRepository, times(1)).findByExternalId(externalId);
    }

    @Test
    @DisplayName("Получение всех пользователей с пагинацией")
    void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleAddress.getStreet(), result.getContent().get(0).getAddress().getStreet());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Поиск пользователей по имени, фамилии или отчеству")
    void testSearchUsers() {
        String search = "Иван";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser), pageable, 1);

        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPatronymicContainingIgnoreCase(
                search, search, search, pageable)).thenReturn(page);

        Page<User> result = userService.searchUsers(search, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleAddress.getStreet(), result.getContent().get(0).getAddress().getStreet());
        verify(userRepository, times(1)).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPatronymicContainingIgnoreCase(
                search, search, search, pageable);
    }

    @Test
    @DisplayName("Получение всех пользователей без пагинации")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(sampleAddress.getStreet(), users.get(0).getAddress().getStreet());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Обновление пользователя с несуществующим externalId должно выбросить исключение")
    void testUpdateByExternalId_NotFound() {
        UUID externalId = UUID.randomUUID();
        Address updatedAddress = Address.builder()
                .street("Невская")
                .house("30")
                .building("C")
                .build();

        User updatedInfo = User.builder()
                .firstName("Алексей")
                .lastName("Алексеев")
                .patronymic("Алексеевич")
                .phone("+7(555)555-55-55")
                .address(updatedAddress)
                .build();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateByExternalId(externalId, updatedInfo));

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