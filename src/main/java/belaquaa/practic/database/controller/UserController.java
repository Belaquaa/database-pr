package belaquaa.practic.database.controller;

import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.mapper.PageConverter;
import belaquaa.practic.database.mapper.UserMapper;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.create(user);
        UserDTO responseDto = userMapper.toDto(createdUser);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID externalId, @Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.updateByExternalId(externalId, user);
        UserDTO responseDto = userMapper.toDto(updatedUser);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID externalId) {
        userService.deleteByExternalId(externalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID externalId) {
        User user = userService.findByExternalId(externalId);
        UserDTO responseDto = userMapper.toDto(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageDto<UserDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "externalId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> userPage;
        if (search != null && !search.isEmpty()) {
            userPage = userService.searchUsers(search, pageRequest);
        } else {
            userPage = userService.findAll(pageRequest);
        }

        PageDto<UserDTO> pageDto = PageConverter.toPageDto(userPage.map(userMapper::toDto));
        return new ResponseEntity<>(pageDto, HttpStatus.OK);
    }
}