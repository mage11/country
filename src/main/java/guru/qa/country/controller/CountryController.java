package guru.qa.country.controller;

import guru.qa.country.model.Country;
import guru.qa.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping("/all")
    public List<Country> getAll(){
        return countryService.getAllCountries();
    }

    @PostMapping("/add")
    public Country add(@RequestBody Country country){
        return countryService.addCountry(country);
    }

    @PatchMapping("/update/{uuid}")
    public Country update(@PathVariable UUID uuid, @RequestBody Country country){
        return countryService.updateCountry(uuid, country);
    }
}
