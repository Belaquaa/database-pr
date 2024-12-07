package belaquaa.practic.database.config;

import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            for (int i = 1; i <= 35; i++) {
                User user = User.builder()
                        .firstName("Имя" + i)
                        .lastName("Фамилия" + i)
                        .patronymic("Отчество" + i)
                        .phone(generateRandomPhone())
                        .address(Address.builder()
                                .street("Улица " + i)
                                .house(String.valueOf(i))
                                .building(i % 3 == 0 ? "Стр." + (i/3) : null)
                                .build())
                        .build();
                userRepository.save(user);
            }
        }
    }

    private String generateRandomPhone() {
        int code = 900 + (int)(Math.random()*100);
        int part1 = 100 + (int)(Math.random()*900);
        int part2 = 10 + (int)(Math.random()*90);
        int part3 = 10 + (int)(Math.random()*90);
        return String.format("+7(%03d)%03d-%02d-%02d", code, part1, part2, part3);
    }
}