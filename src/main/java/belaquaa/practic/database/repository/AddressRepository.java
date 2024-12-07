package belaquaa.practic.database.repository;

import belaquaa.practic.database.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}