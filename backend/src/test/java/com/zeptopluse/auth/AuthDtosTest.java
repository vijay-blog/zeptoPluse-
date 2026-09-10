package com.zeptopluse.auth;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthDtosTest {
    @Test void registrationRequiresEightCharacterPassword() {
        var validator=Validation.buildDefaultValidatorFactory().getValidator();
        var violations=validator.validate(new AuthDtos.RegisterRequest("Raghu","raghu@example.com","9876543210","short"));
        assertTrue(violations.stream().anyMatch(v->v.getPropertyPath().toString().equals("password")));
    }
}
