package fr.efrei.rag.web.rest;

import fr.efrei.rag.web.dto.Sample;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleResource {

    @GetMapping("/samples/{value}")
    public String hello(@PathVariable(value = "value",required = false)final String value) {
        return "Hello World " + value +" !";
    }

    @GetMapping("/samples/dto/{value}")
    public Sample helloJson(@PathVariable(value = "value", required = false) final String value) {
        return new Sample("Hello " + value + "!");
    }
}