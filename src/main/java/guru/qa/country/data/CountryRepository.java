package guru.qa.country.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    CountryEntity findByCountryName(String countryName);
}
