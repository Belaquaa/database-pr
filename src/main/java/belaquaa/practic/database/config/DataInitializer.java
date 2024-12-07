package belaquaa.practic.database.config;

import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Set<String> generatedPhones = new HashSet<>();
            for (int i = 1; i <= 35; i++) {
                String phone;
                do {
                    phone = generateRandomPhone();
                } while (generatedPhones.contains(phone));
                generatedPhones.add(phone);

                User user = User.builder()
                        .firstName("Имя" + i)
                        .lastName("Фамилия" + i)
                        .patronymic("Отчество" + i)
                        .phone(phone)
                        .address(Address.builder()
                                .street("Улица " + i)
                                .house(String.valueOf(i))
                                .building(i % 3 == 0 ? "Стр." + (i / 3) : null)
                                .build())
                        .build();
                userRepository.save(user);
            }
        }
    }

    private String generateRandomPhone() {
        StringBuilder sb = new StringBuilder("7");
        for (int i = 0; i < 10; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}