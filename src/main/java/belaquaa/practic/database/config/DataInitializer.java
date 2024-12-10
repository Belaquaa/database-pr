package belaquaa.practic.database.config;

import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import belaquaa.practic.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    // Списки мужских и женских имён
    private static final List<String> FIRST_NAMES_MALE = Arrays.asList(
            "Александр", "Максим", "Сергей", "Дмитрий", "Андрей",
            "Михаил", "Иван", "Егор", "Никита", "Владимир",
            "Пётр", "Константин", "Олег", "Юрий", "Антон"
    );
    private static final List<String> FIRST_NAMES_FEMALE = Arrays.asList(
            "Анна", "Мария", "Елена", "Ольга", "Татьяна",
            "Наталья", "Ирина", "Светлана", "Ксения", "Виктория",
            "Юлия", "Дарья", "Анастасия", "Екатерина", "Людмила"
    );
    // Списки мужских и женских фамилий
    private static final List<String> LAST_NAMES_MALE = Arrays.asList(
            "Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев",
            "Петров", "Соколов", "Михайлов", "Новиков", "Фёдоров",
            "Морозов", "Волков", "Александров", "Лебедев", "Семенов"
    );
    private static final List<String> LAST_NAMES_FEMALE = Arrays.asList(
            "Иванова", "Смирнова", "Кузнецова", "Попова", "Васильева",
            "Петрова", "Соколова", "Михайлова", "Новикова", "Фёдорова",
            "Морозова", "Волкова", "Александрова", "Лебедева", "Семенова"
    );
    // Списки мужских и женских отчеств
    private static final List<String> PATRONYMICS_MALE = Arrays.asList(
            "Александрович", "Максимович", "Сергеевич", "Дмитриевич", "Андреевич",
            "Михайлович", "Иванович", "Егорович", "Никитич", "Владимирович",
            "Петрович", "Константинович", "Олегович", "Юрьевич", "Антонович"
    );
    private static final List<String> PATRONYMICS_FEMALE = Arrays.asList(
            "Александровна", "Максимовна", "Сергеевна", "Дмитриевна", "Андреевна",
            "Михайловна", "Ивановна", "Егоровна", "Никитична", "Владимировна",
            "Петровна", "Константиновна", "Олеговна", "Юрьевна", "Антоновна"
    );
    private static final List<String> STREETS = Arrays.asList(
            "Ленина", "Советская", "Московская", "Пушкина", "Светлая",
            "Центральная", "Кирова", "Невский", "Гагарина", "Комсомольская",
            "Чапаева", "Фрунзе", "Красная", "Луначарского", "Октябрьская"
    );
    private static final Random RANDOM = new Random();
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Set<String> generatedPhones = new HashSet<>();
            int numberOfRandomUsers = 35;
            for (int i = 0; i < numberOfRandomUsers; i++) {
                String phone;
                do {
                    phone = generateRandomPhone();
                } while (generatedPhones.contains(phone));
                generatedPhones.add(phone);

                boolean isMale = RANDOM.nextBoolean();
                String firstName = isMale
                        ? FIRST_NAMES_MALE.get(RANDOM.nextInt(FIRST_NAMES_MALE.size()))
                        : FIRST_NAMES_FEMALE.get(RANDOM.nextInt(FIRST_NAMES_FEMALE.size()));
                String lastName = isMale
                        ? LAST_NAMES_MALE.get(RANDOM.nextInt(LAST_NAMES_MALE.size()))
                        : LAST_NAMES_FEMALE.get(RANDOM.nextInt(LAST_NAMES_FEMALE.size()));
                String patronymic = isMale
                        ? PATRONYMICS_MALE.get(RANDOM.nextInt(PATRONYMICS_MALE.size()))
                        : PATRONYMICS_FEMALE.get(RANDOM.nextInt(PATRONYMICS_FEMALE.size()));
                String street = STREETS.get(RANDOM.nextInt(STREETS.size()));
                String house = String.valueOf(RANDOM.nextInt(200) + 1);
                String building = RANDOM.nextInt(3) == 0 ? "Стр." + (RANDOM.nextInt(10) + 1) : null;

                User user = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .patronymic(patronymic)
                        .phone(phone)
                        .address(Address.builder()
                                .street("ул. " + street)
                                .house(house)
                                .building(building)
                                .build())
                        .build();
                userRepository.save(user);
            }

            String phone1;
            do {
                phone1 = generateRandomPhone();
            } while (generatedPhones.contains(phone1));
            generatedPhones.add(phone1);

            User user1 = User.builder()
                    .firstName("Владислав")
                    .lastName("Колесников")
                    .patronymic("Сергеевич")
                    .phone(phone1)
                    .address(Address.builder()
                            .street("ул. Пушкина")
                            .house("10")
                            .building("Стр.1")
                            .build())
                    .build();
            userRepository.save(user1);

            String phone2;
            do {
                phone2 = generateRandomPhone();
            } while (generatedPhones.contains(phone2));
            generatedPhones.add(phone2);

            User user2 = User.builder()
                    .firstName("Динара")
                    .lastName("Матыгулина")
                    .patronymic("Тагировна")
                    .phone(phone2)
                    .address(Address.builder()
                            .street("ул. Ленина")
                            .house("25")
                            .building(null)
                            .build())
                    .build();
            userRepository.save(user2);
        }
    }

    private String generateRandomPhone() {
        StringBuilder sb = new StringBuilder("7");
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}