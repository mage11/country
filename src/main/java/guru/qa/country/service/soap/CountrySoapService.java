package guru.qa.country.service.soap;
import guru.qa.country.config.CountryApplicationConfig;
import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.xml.country.AddRequest;
import guru.qa.xml.country.AddResponse;
import guru.qa.xml.country.AllResponse;
import guru.qa.xml.country.CountryByIdRequest;
import guru.qa.xml.country.CountryByIdResponse;
import guru.qa.xml.country.CountryResponse;
import guru.qa.xml.country.UpdateRequest;
import guru.qa.xml.country.UpdateResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Endpoint
public class CountrySoapService {

    private final CountryRepository countryRepository;

    public CountrySoapService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PayloadRoot(namespace = CountryApplicationConfig.SOAP_NAMESPACE, localPart = "CountryByIdRequest")
    @ResponsePayload
    public CountryByIdResponse country(@RequestPayload CountryByIdRequest request) {
        CountryEntity country = countryRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new NoSuchElementException("По id:" + request.getId()+ " ничего не было найдено"));
        CountryByIdResponse xmlCountry = new CountryByIdResponse();
        xmlCountry.setId(country.getId().toString());
        xmlCountry.setCountryName(country.getCountryName());
        xmlCountry.setCountryCode(country.getCountryCode());
        return xmlCountry;
    }

    @PayloadRoot(namespace = CountryApplicationConfig.SOAP_NAMESPACE, localPart = "AllRequest")
    @ResponsePayload
    public AllResponse all() {
        List<CountryEntity> allCountries = countryRepository.findAll();
        AllResponse xmlCountries = new AllResponse();
        xmlCountries.getCountry().addAll(
            allCountries.stream().map(countryEntity -> {
            CountryResponse country = new CountryResponse();
            country.setId(countryEntity.getId().toString());
            country.setCountryName(countryEntity.getCountryName());
            country.setCountryCode(countryEntity.getCountryCode());
            return country;
        }).toList());
        return xmlCountries;
    }

    @PayloadRoot(namespace = CountryApplicationConfig.SOAP_NAMESPACE, localPart = "AddRequest")
    @ResponsePayload
    public AddResponse add(@RequestPayload AddRequest request) {
        CountryEntity countryEntity = new CountryEntity(
            null,
            request.getCountry().getCountryName(),
            request.getCountry().getCountryCode());
        countryRepository.save(countryEntity);
        AddResponse addResponse = new AddResponse();
        CountryEntity createdCountry = countryRepository.save(countryEntity);
        addResponse.setId(createdCountry.getId().toString());
        addResponse.setCountryName(createdCountry.getCountryName());
        addResponse.setCountryCode(createdCountry.getCountryCode());
        return addResponse;
    }

    @PayloadRoot(namespace = CountryApplicationConfig.SOAP_NAMESPACE, localPart = "UpdateRequest")
    @ResponsePayload
    public UpdateResponse update(@RequestPayload UpdateRequest request) {
        CountryEntity countryEntity = countryRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new NoSuchElementException("По id:" + request.getId() + " ничего не было найдено"));
        CountryEntity updatedCountryEntity = new CountryEntity(
            countryEntity.getId(),
            request.getCountry().getCountryName(),
            request.getCountry().getCountryCode());
        CountryEntity updatedCountry = countryRepository.save(updatedCountryEntity);
        UpdateResponse response = new UpdateResponse();
        CountryResponse countryResponse = new CountryResponse();
        countryResponse.setId(updatedCountry.getId().toString());
        countryResponse.setCountryName(updatedCountry.getCountryName());
        countryResponse.setCountryCode(updatedCountry.getCountryCode());
        response.setCountry(countryResponse);
        return response;
    }


}
