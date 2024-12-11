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

    public String formatPhoneForOutput(String phone) {
        if (phone == null || phone.length() != 11 || !phone.startsWith("7")) {
            return phone;
        }

        String code = phone.substring(1, 4);
        String part1 = phone.substring(4, 7);
        String part2 = phone.substring(7, 9);
        String part3 = phone.substring(9, 11);
        return "+7(" + code + ")" + part1 + "-" + part2 + "-" + part3;
    }
}