package belaquaa.practic.database.formatter;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {

    public String normalizePhone(String input) {
        if (input == null) {
            return null;
        }

        String digits = input.replaceAll("[^\\d+]", "");

        if (digits.startsWith("8")) {
            digits = "+7" + digits.substring(1);
        }

        if (!digits.startsWith("+7")) {
            if (digits.startsWith("7")) {
                digits = "+7" + digits.substring(1);
            } else if (digits.matches("^\\d{10}$")) {
                digits = "+7" + digits;
            }
        }

        digits = digits.replaceAll("[^\\d]", "");
        if (digits.startsWith("7")) {
            digits = "7" + digits.substring(1);
        }

        if (digits.length() == 11) {
            // digits[0] = 7
            String code = digits.substring(1, 4);
            String part1 = digits.substring(4, 7);
            String part2 = digits.substring(7, 9);
            String part3 = digits.substring(9, 11);
            return "+7(" + code + ")" + part1 + "-" + part2 + "-" + part3;
        }

        return "+7(000)000-00-00"; // fallback
    }
}