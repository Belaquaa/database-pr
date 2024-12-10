package belaquaa.practic.database.mapper;

import belaquaa.practic.database.formatter.PhoneNumberFormatter;
import belaquaa.practic.database.model.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEntityListener {

    private static PhoneNumberFormatter phoneNumberFormatter;

    @Autowired
    public void setPhoneNumberFormatter(PhoneNumberFormatter formatter) {
        phoneNumberFormatter = formatter;
    }

    @PrePersist
    @PreUpdate
    public void normalizeData(User user) {
        if (user.getExternalId() == null) {
            user.setExternalId(UUID.randomUUID());
        }
        if (user.getPhone() != null) {
            user.setPhone(phoneNumberFormatter.normalizePhone(user.getPhone()));
        }
    }
}
