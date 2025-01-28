package guru.qa.country.service;

import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.country.model.Country;
import guru.qa.grpc.country.AllCountriesRequest;
import guru.qa.grpc.country.AllCountriesResponse;
import guru.qa.grpc.country.CountCreatedCountries;
import guru.qa.grpc.country.CountryGrpc;
import guru.qa.grpc.country.CountryRequest;
import guru.qa.grpc.country.CountryResponse;
import guru.qa.grpc.country.CountryServiceGrpc;
import guru.qa.grpc.country.UpdateCountryRequest;
import guru.qa.grpc.country.idRequest;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryRepository countryRepository;

    public CountryGrpcService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getCountry(idRequest request, StreamObserver<CountryResponse> responseObserver) {
        Country country = Country.fromEntity(countryRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new NoSuchElementException("По id:" + request.getId()+ " ничего не было найдено")));

        responseObserver.onNext(
            CountryResponse.newBuilder()
                .setCountry(countryConverterToGrpc(country))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void all(AllCountriesRequest request, StreamObserver<AllCountriesResponse> responseObserver) {
        List<Country> allCountries = countryRepository.findAll()
            .stream()
            .map(Country::fromEntity).toList();
        List<CountryGrpc> allCountriesResponseList =
            allCountries.stream()
                .map(this::countryConverterToGrpc)
                    .collect(Collectors.toList());

        responseObserver.onNext(
            AllCountriesResponse.newBuilder()
                .addAllCountries(allCountriesResponseList)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CountryRequest> add(StreamObserver<CountCreatedCountries> responseObserver) {
        AtomicInteger totalAdded = new AtomicInteger(0);

        return new StreamObserver<CountryRequest>() {
            @Override
            public void onNext(CountryRequest country) {
                CountryEntity countryEntity = new CountryEntity(
                    null,
                    country.getCountryName(),
                    country.getCountryCode());
                countryRepository.save(countryEntity);
                totalAdded.incrementAndGet();
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                CountCreatedCountries response = CountCreatedCountries.newBuilder()
                    .setCount(totalAdded.get())
                    .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }


    @Override
    public void update(UpdateCountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        CountryEntity countryEntity = countryRepository.findById(UUID.fromString(request.getId()))
            .orElseThrow(() -> new NoSuchElementException("По id:" + request.getId() + " ничего не было найдено"));

        CountryEntity updatedCountryEntity = new CountryEntity(
            countryEntity.getId(),
            request.getCountry().getCountryName(),
            request.getCountry().getCountryCode());

        responseObserver.onNext(
            CountryResponse.newBuilder()
                .setCountry(
                    countryConverterToGrpc(Country.fromEntity(countryRepository.save(updatedCountryEntity))))
                .build());
        responseObserver.onCompleted();
    }

    private CountryGrpc countryConverterToGrpc(Country country) {
        return CountryGrpc.newBuilder()
            .setId(country.id().toString())
            .setCountryCode(country.countryCode())
            .setCountryName(country.countryName())
            .build();
    }

    private Country countryConverterToDefaultType(CountryRequest country) {
        return new Country(null, country.getCountryName(),country.getCountryCode());
    }
}
