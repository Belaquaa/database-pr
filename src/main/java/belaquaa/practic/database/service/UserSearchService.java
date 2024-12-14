package belaquaa.practic.database.service;

import belaquaa.practic.database.formatter.KeyboardLayoutService;
import belaquaa.practic.database.formatter.SimilarityService;
import belaquaa.practic.database.formatter.TransliterationService;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService {

    private final UserRepository userRepository;
    private final TransliterationService transliterationService;
    private final KeyboardLayoutService keyboardLayoutService;
    private final SimilarityService similarityService;

    public Page<User> searchUsers(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return Page.empty();
        }

        String[] terms = search.trim().split("\\s+");
        log.info("Термины поиска: {}", Arrays.toString(terms));

        boolean isRegex = containsRegexSymbols(search);

        if (isRegex) {
            return userRepository.searchUsersWithRegex(search, pageable);
        } else {
            Specification<User> spec = Arrays.stream(terms)
                    .map(this::generateVariants)
                    .map(this::buildTermSpecification)
                    .reduce(Specification::and)
                    .orElse(Specification.where(null));

            Page<User> initialResults = userRepository.findAll(spec, pageable);

            List<User> filteredUsers = initialResults.stream()
                    .filter(user -> Arrays.stream(terms).allMatch(term -> isUserSimilar(user, generateVariants(term))))
                    .toList();

            return new PageImpl<>(filteredUsers, pageable, filteredUsers.size());
        }
    }

    private boolean containsRegexSymbols(String input) {
        return input.contains("*") || input.contains("+") || input.contains("?") || input.contains("{") ||
                input.contains("}") || input.contains("[") || input.contains("]") || input.contains("(") ||
                input.contains(")") || input.contains("|") || input.contains("\\") || input.contains("^") ||
                input.contains("$") || input.contains(".");
    }

    private Specification<User> buildTermSpecification(List<String> termVariants) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String variant : termVariants) {
                if (variant == null || variant.isEmpty()) continue;
                String pattern = "%" + variant.toLowerCase() + "%";

                predicates.add(cb.like(cb.lower(root.get("firstName")), pattern));
                predicates.add(cb.like(cb.lower(root.get("lastName")), pattern));
                predicates.add(cb.like(cb.lower(root.get("patronymic")), pattern));
                predicates.add(cb.like(cb.lower(root.get("phone")), pattern));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> generateVariants(String input) {
        List<String> variants = new ArrayList<>();
        variants.add(input);

        String translit = transliterationService.transliterateToLatin(input);
        String translitBack = transliterationService.transliterateToCyrillic(input);
        String layout = keyboardLayoutService.convertLayoutToRussian(input);

        if (translit != null && !translit.equalsIgnoreCase(input)) {
            variants.add(translit);
        }
        if (layout != null && !layout.equalsIgnoreCase(input)) {
            variants.add(layout);
        }
        if (translitBack != null && !translitBack.equalsIgnoreCase(input)) {
            variants.add(translitBack);
        }

        return variants;
    }

    private boolean isUserSimilar(User user, List<String> termVariants) {
        return matchesFields(user, termVariants) || matchesPhone(user, termVariants);
    }

    private boolean matchesFields(User user, List<String> searchForms) {
        List<String> userFields = Arrays.asList(user.getFirstName(), user.getLastName(), user.getPatronymic());
        return userFields.stream()
                .filter(Objects::nonNull)
                .flatMap(field -> generateVariants(field).stream())
                .anyMatch(fieldVariant -> searchForms.stream()
                        .anyMatch(search -> fieldVariant.toLowerCase().contains(search.toLowerCase())
                                && similarityService.isSimilar(fieldVariant, search)));
    }

    private boolean matchesPhone(User user, List<String> searchForms) {
        if (user.getPhone() == null) return false;
        String phoneLower = user.getPhone().toLowerCase();
        return searchForms.stream().anyMatch(search -> search != null && phoneLower.contains(search.toLowerCase()));
    }
}