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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService {

    private final UserRepository userRepository;
    private final TransliterationService transliterationService;
    private final KeyboardLayoutService keyboardLayoutService;
    private final SimilarityService similarityService;

    public Page<User> searchUsers(String search, Pageable pageable) {

        // Генерация различных вариантов поискового запроса
        String translitSearch = transliterationService.transliterateToLatin(search);
        String translitBackSearch = transliterationService.transliterateToCyrillic(search);
        String layoutSearch = keyboardLayoutService.convertLayoutToRussian(search);

        // Выполнение поиска по всем вариантам
        Page<User> initialResults = userRepository.searchByNameOrPhone(search, translitSearch, layoutSearch, pageable);

        // Дополнительный поиск, если обратная транслитерация отличается
        if (!translitBackSearch.equalsIgnoreCase(search)) {
            Page<User> additionalResults = userRepository.searchByNameOrPhone(translitBackSearch, search, translitBackSearch, pageable);
            initialResults = mergePages(initialResults, additionalResults, pageable);
        }

        // Составляем список всех форм поискового запроса
        List<String> searchForms = new ArrayList<>();
        searchForms.add(search);
        searchForms.add(translitSearch);
        searchForms.add(layoutSearch);
        if (!translitBackSearch.equalsIgnoreCase(search)) {
            searchForms.add(translitBackSearch);
        }

        // Фильтрация результатов по расстоянию Левенштейна
        List<User> filteredUsers = initialResults.stream()
                .filter(user -> isUserSimilar(user, searchForms))
                .toList();

        return new PageImpl<>(filteredUsers, pageable, filteredUsers.size());
    }


    private Page<User> mergePages(Page<User> page1, Page<User> page2, Pageable pageable) {
        List<User> combined = new ArrayList<>();
        combined.addAll(page1.getContent());
        combined.addAll(page2.getContent());
        combined = combined.stream()
                .distinct()
                .collect(Collectors.toList());
        return new PageImpl<>(combined, pageable, combined.size());
    }

    private boolean isUserSimilar(User user, List<String> searchForms) {
        for (String search : searchForms) {
            String searchLower = search.toLowerCase();

            // Проверка, начинается ли любое из полей с поискового запроса
            if ((user.getFirstName() != null && user.getFirstName().toLowerCase().startsWith(searchLower)) ||
                    (user.getLastName() != null && user.getLastName().toLowerCase().startsWith(searchLower)) ||
                    (user.getPatronymic() != null && user.getPatronymic().toLowerCase().startsWith(searchLower))) {
                return true;
            }

            // Проверка схожести оригинальных полей с поисковым запросом
            if (similarityService.isSimilar(user.getFirstName(), search) ||
                    similarityService.isSimilar(user.getLastName(), search) ||
                    similarityService.isSimilar(user.getPatronymic(), search)) {
                return true;
            }

            // Получение транслитерированных полей
            String translitFirstName = transliterationService.transliterateToLatin(user.getFirstName());
            String translitLastName = transliterationService.transliterateToLatin(user.getLastName());
            String translitPatronymic = transliterationService.transliterateToLatin(user.getPatronymic());

            // Проверка, начинается ли транслитерированное поле с поискового запроса
            if ((translitFirstName != null && translitFirstName.toLowerCase().startsWith(searchLower)) ||
                    (translitLastName != null && translitLastName.toLowerCase().startsWith(searchLower)) ||
                    (translitPatronymic != null && translitPatronymic.toLowerCase().startsWith(searchLower))) {
                return true;
            }

            // Проверка схожести транслитерированных полей с поисковым запросом
            if (similarityService.isSimilar(translitFirstName, search) ||
                    similarityService.isSimilar(translitLastName, search) ||
                    similarityService.isSimilar(translitPatronymic, search)) {
                return true;
            }

            // Получение полей, преобразованных из раскладки
            String layoutFirstName = keyboardLayoutService.convertLayoutToRussian(user.getFirstName());
            String layoutLastName = keyboardLayoutService.convertLayoutToRussian(user.getLastName());
            String layoutPatronymic = keyboardLayoutService.convertLayoutToRussian(user.getPatronymic());

            // Проверка, начинается ли поле, преобразованное из раскладки, с поискового запроса
            if ((layoutFirstName != null && layoutFirstName.toLowerCase().startsWith(searchLower)) ||
                    (layoutLastName != null && layoutLastName.toLowerCase().startsWith(searchLower)) ||
                    (layoutPatronymic != null && layoutPatronymic.toLowerCase().startsWith(searchLower))) {
                return true;
            }

            // Проверка схожести полей, преобразованных из раскладки, с поисковым запросом
            if (similarityService.isSimilar(layoutFirstName, search) ||
                    similarityService.isSimilar(layoutLastName, search) ||
                    similarityService.isSimilar(layoutPatronymic, search)) {
                return true;
            }
        }

        // Обработка поля phone отдельно без использования SimilarityService
        for (String search : searchForms) {
            if (user.getPhone() != null && user.getPhone().toLowerCase().contains(search.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}