package belaquaa.practic.database.controller;

import belaquaa.practic.database.dto.PageDto;
import belaquaa.practic.database.formatter.PageConverter;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.create(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID externalId, @Valid @RequestBody User user) {
        User updatedUser = userService.updateByExternalId(externalId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID externalId) {
        userService.deleteByExternalId(externalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<User> getUser(@PathVariable UUID externalId) {
        User user = userService.findByExternalId(externalId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageDto<User>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "externalId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (search != null && !search.isEmpty()) {
            return new ResponseEntity<>(PageConverter.toPageDto(userService.searchUsers(search, pageRequest)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(PageConverter.toPageDto(userService.findAll(pageRequest)), HttpStatus.OK);
        }
    }
}