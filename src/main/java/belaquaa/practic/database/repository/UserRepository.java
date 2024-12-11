package belaquaa.practic.database.repository;

import belaquaa.practic.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByExternalId(UUID externalId);

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPatronymicContainingIgnoreCaseOrPhoneContaining(
            String firstName, String lastName, String patronymic, String phone, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :translitSearch, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :translitSearch, '%')) OR " +
            "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :translitSearch, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :layoutSearch, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :layoutSearch, '%')) OR " +
            "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :layoutSearch, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :translitBackSearch, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :translitBackSearch, '%')) OR " +
            "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :translitBackSearch, '%'))")
    Page<User> searchByAllSearchTerms(@Param("search") String search,
                                      @Param("translitSearch") String translitSearch,
                                      @Param("layoutSearch") String layoutSearch,
                                      @Param("translitBackSearch") String translitBackSearch,
                                      Pageable pageable);
}