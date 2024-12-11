package belaquaa.practic.database.service;

import belaquaa.practic.database.formatter.KeyboardLayoutService;
import belaquaa.practic.database.formatter.SimilarityService;
import belaquaa.practic.database.formatter.TransliterationService;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService {

    private final UserRepository userRepository;
    private final TransliterationService transliterationService;
    private final KeyboardLayoutService keyboardLayoutService;
    private final SimilarityService similarityService;

    public Page<User> searchUsers(String search, Pageable pageable) {
        List<String> searchForms = generateVariants(search);
        Page<User> initialResults = userRepository.searchByAllSearchTerms(
                search,
                transliterationService.transliterateToLatin(search),
                keyboardLayoutService.convertLayoutToRussian(search),
                transliterationService.transliterateToCyrillic(search),
                pageable
        );
        List<User> filteredUsers = initialResults.stream()
                .filter(user -> isUserSimilar(user, searchForms))
                .toList();
        return new PageImpl<>(filteredUsers, pageable, filteredUsers.size());
    }

    private List<String> generateVariants(String input) {
        List<String> variants = new ArrayList<>();
        variants.add(input);

        String translit = transliterationService.transliterateToLatin(input);
        String translitBack = transliterationService.transliterateToCyrillic(input);
        String layout = keyboardLayoutService.convertLayoutToRussian(input);

        if (translit != null && !translit.equalsIgnoreCase(input)) {
            log.info("Translit: {}", translit);
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

    private boolean isUserSimilar(User user, List<String> searchForms) {
        return matchesFields(user, searchForms) || matchesPhone(user, searchForms);
    }

    private boolean matchesFields(User user, List<String> searchForms) {
        List<String> userFields = Arrays.asList(user.getFirstName(), user.getLastName(), user.getPatronymic());

        for (String field : userFields) {
            if (field == null) continue;
            List<String> fieldVariants = generateVariants(field);

            for (String variant : fieldVariants) {
                if (variant == null) continue;
                String variantLower = variant.toLowerCase();

                for (String search : searchForms) {
                    String searchLower = search.toLowerCase();

                    if (variantLower.startsWith(searchLower)) {
                        return true;
                    }

                    if (similarityService.isSimilar(variant, search)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean matchesPhone(User user, List<String> searchForms) {
        if (user.getPhone() == null) return false;
        String phoneLower = user.getPhone().toLowerCase();

        return searchForms.stream()
                .anyMatch(search -> search != null && phoneLower.contains(search.toLowerCase()));
    }
}
