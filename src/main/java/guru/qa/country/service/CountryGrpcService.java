package guru.qa.country.service;

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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryService countryService;

    public CountryGrpcService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void getCountry(idRequest request, StreamObserver<CountryResponse> responseObserver) {
        Country country = countryService.getCountryById(UUID.fromString(request.getId()));

        responseObserver.onNext(
            CountryResponse.newBuilder()
                .setCountry(countryConverterToGrpc(country))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void all(AllCountriesRequest request, StreamObserver<AllCountriesResponse> responseObserver) {
        List<Country> allCountries = countryService.getAllCountries();
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
                countryService.addCountry(countryConverterToDefaultType(country));
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
        Country country = countryService.updateCountry(
            UUID.fromString(request.getId()), countryConverterToDefaultType(request.getCountry()));

        responseObserver.onNext(
            CountryResponse.newBuilder()
                .setCountry(
                    countryConverterToGrpc(country))
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
