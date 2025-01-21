package guru.qa.country.controller.graphql;

import guru.qa.country.model.Country;
import guru.qa.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Controller
public class CountryQueryController {

    private final CountryService countryService;

    @Autowired
    public CountryQueryController(CountryService countryService){
        this.countryService = countryService;
    }

    @QueryMapping
    public List<Country> all(){
        return countryService.getAllCountries();
    }

    @QueryMapping
    public Country country(@Argument UUID id) {
        return countryService.getCountryById(id);
    }
}
