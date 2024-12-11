package belaquaa.practic.database.mapper;

import belaquaa.practic.database.dto.AddressDTO;
import belaquaa.practic.database.dto.UserDTO;
import belaquaa.practic.database.formatter.PhoneNumberFormatter;
import belaquaa.practic.database.model.Address;
import belaquaa.practic.database.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PhoneNumberFormatter phoneNumberFormatter;

    public UserDTO toDto(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .externalId(user.getExternalId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymic())
                .phone(phoneNumberFormatter.formatPhoneForOutput(user.getPhone()))
                .address(toDto(user.getAddress()))
                .build();
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User.UserBuilder userBuilder = User.builder()
                .firstName(capitalizeFirstLetter(dto.getFirstName()))
                .lastName(capitalizeFirstLetter(dto.getLastName()))
                .patronymic(capitalizeFirstLetter(dto.getPatronymic()))
                .phone(phoneNumberFormatter.normalizePhone(dto.getPhone()))
                .address(toEntity(dto.getAddress()));

        assignExternalId(dto, userBuilder);

        return userBuilder.build();
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
        return users.stream().map(this::toDto).toList();
    }

    private String capitalizeFirstLetter(String value) {
        if (value == null || value.isEmpty()) return value;
        return WordUtils.capitalizeFully(value.toLowerCase());
    }

    private void assignExternalId(UserDTO dto, User.UserBuilder userBuilder) {
        if (dto.getExternalId() == null) {
            userBuilder.externalId(UUID.randomUUID());
        } else {
            userBuilder.externalId(dto.getExternalId());
        }
    }
}