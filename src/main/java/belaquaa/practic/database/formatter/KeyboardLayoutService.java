package belaquaa.practic.database.formatter;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KeyboardLayoutService {

    private static final Map<Character, Character> ENGLISH_TO_RUSSIAN_MAP = createEnglishToRussianMap();

    private static Map<Character, Character> createEnglishToRussianMap() {
        Map<Character, Character> map = new HashMap<>();
        map.put('q', 'й');
        map.put('w', 'ц');
        map.put('e', 'у');
        map.put('r', 'к');
        map.put('t', 'е');
        map.put('y', 'н');
        map.put('u', 'г');
        map.put('i', 'ш');
        map.put('o', 'щ');
        map.put('p', 'з');
        map.put('[', 'х');
        map.put(']', 'ъ');
        map.put('a', 'ф');
        map.put('s', 'ы');
        map.put('d', 'в');
        map.put('f', 'а');
        map.put('g', 'п');
        map.put('h', 'р');
        map.put('j', 'о');
        map.put('k', 'л');
        map.put('l', 'д');
        map.put(';', 'ж');
        map.put('\'', 'э');
        map.put('z', 'я');
        map.put('x', 'ч');
        map.put('c', 'с');
        map.put('v', 'м');
        map.put('b', 'и');
        map.put('n', 'т');
        map.put('m', 'ь');
        map.put(',', 'б');
        map.put('.', 'ю');
        map.put('/', '.');
        map.put('ё', 'е');
        return map;
    }

    public String convertLayoutToRussian(String input) {
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            char lowerCh = Character.toLowerCase(ch);
            if (ENGLISH_TO_RUSSIAN_MAP.containsKey(lowerCh)) {
                char russianChar = ENGLISH_TO_RUSSIAN_MAP.get(lowerCh);
                if (Character.isUpperCase(ch)) {
                    russianChar = Character.toUpperCase(russianChar);
                }
                sb.append(russianChar);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
