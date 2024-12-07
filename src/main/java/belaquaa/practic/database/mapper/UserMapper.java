package belaquaa.practic.database.mapper;

import belaquaa.practic.database.dto.AddressDTO;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.formatter.PhoneNumberFormatter;
import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final PhoneNumberFormatter phoneNumberFormatter;

    public UserMapper(PhoneNumberFormatter phoneNumberFormatter) {
        this.phoneNumberFormatter = phoneNumberFormatter;
    }

    public UserDTO toDto(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .externalId(user.getExternalId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymic())
                .phone(formatPhoneForOutput(user.getPhone()))
                .address(toDto(user.getAddress()))
                .build();
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .externalId(dto.getExternalId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .patronymic(dto.getPatronymic())
                .phone(dto.getPhone())
                .address(toEntity(dto.getAddress()))
                .build();
    }

    public AddressDTO toDto(Address address) {
        if (address == null) return null;

        return AddressDTO.builder()
                .street(address.getStreet())
                .house(address.getHouse())
                .building(address.getBuilding())
                .build();
    }

    public Address toEntity(AddressDTO dto) {
        if (dto == null) return null;

        return Address.builder()
                .street(dto.getStreet())
                .house(dto.getHouse())
                .building(dto.getBuilding())
                .build();
    }

    public List<UserDTO> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Форматирует номер телефона для вывода клиенту.
     * Например, '79135334455' -> '+7(913)533-44-55'
     *
     * @param phone Нормализованный номер телефона.
     * @return Отформатированный номер телефона.
     */
    private String formatPhoneForOutput(String phone) {
        if (phone == null || phone.length() != 11 || !phone.startsWith("7")) {
            return phone; // Возвращаем как есть или можно вернуть null/ошибку
        }

        String code = phone.substring(1, 4);
        String part1 = phone.substring(4, 7);
        String part2 = phone.substring(7, 9);
        String part3 = phone.substring(9, 11);
        return "+7(" + code + ")" + part1 + "-" + part2 + "-" + part3;
    }
}