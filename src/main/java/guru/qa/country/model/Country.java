package guru.qa.country.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.country.data.CountryEntity;

import java.util.UUID;


public record Country(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("country_name")
    String countryName,
    @JsonProperty("country_code")
    String countryCode
) {

    public static Country fromEntity(CountryEntity ce) {
        return new Country(
            ce.getId(),
            ce.getCountryName(),
            ce.getCountryCode()
        );
    }
}
