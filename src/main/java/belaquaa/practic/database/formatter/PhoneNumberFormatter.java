package belaquaa.practic.database.formatter;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {

    public String normalizePhone(String input) {
        if (input == null) {
            return null;
        }

        String digits = input.replaceAll("[^\\d]", "");

        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }

        if (digits.length() == 10) {
            digits = "7" + digits;
        }

        if (digits.length() == 11 && digits.startsWith("7")) {
            return digits;
        }

        throw new IllegalArgumentException("Некорректный формат номера телефона: " + input);
    }
}