package belaquaa.practic.database.controller;

import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID externalId, @Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.updateByExternalId(externalId, userDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID externalId) {
        userService.deleteByExternalId(externalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID externalId) {
        return new ResponseEntity<>(userService.findByExternalId(externalId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageDto<UserDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "externalId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        Pageable pageRequest = PageRequest.of(page, size);
        PageDto<UserDTO> pageDto = search != null && !search.isEmpty()
                ? userService.searchUsers(search, pageRequest, sortField, sortDir)
                : userService.findAll(pageRequest, sortField, sortDir);
        return new ResponseEntity<>(pageDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
}