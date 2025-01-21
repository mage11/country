package guru.qa.country.controller.graphql;

import guru.qa.country.model.Country;
import guru.qa.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Controller
public class CountryMutationController {

    private final CountryService countryService;

    @Autowired
    public CountryMutationController(CountryService countryService){
        this.countryService = countryService;
    }

    @MutationMapping
    public Country add(@Argument Country country){
        return countryService.addCountry(country);
    }

    @MutationMapping
    public Country update(@Argument UUID id, @Argument Country country){
        return countryService.updateCountry(id, country);
    }
}
