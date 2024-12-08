package guru.qa.country.service;

import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.country.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    public List<Country> getAllCountries(){
        return countryRepository.findAll()
            .stream()
            .map(Country::fromEntity).toList();
    }

    public Country addCountry(Country country){
        CountryEntity countryEntity = new CountryEntity(
            null,
            country.countryName(),
            country.countryCode());

        return Country.fromEntity(countryRepository.save(countryEntity));
    }

    public Country updateCountry(UUID uuid, Country country){
        CountryEntity countryEntity = countryRepository.findById(uuid)
            .orElseThrow(() -> new NoSuchElementException("По id:" + uuid + " ничего не было найдено"));

        CountryEntity updatedCountryEntity = new CountryEntity(
            countryEntity.getId(),
            country.countryName(),
            country.countryCode());

        return Country.fromEntity(countryRepository.save(updatedCountryEntity));
    }
}
