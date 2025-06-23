package br.edu.ifsp.spo.bike_integration.service.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;

@Service
public class AccessKeyValidateService {

    @Value("${x.access-key}")
    private String accessKey;

    public boolean isValid(String accessKey, RoleType... roles) {
        return this.accessKey.equals(accessKey) && roles.length > 0
                && Arrays.stream(roles).anyMatch(role -> role.equals(RoleType.ADMIN));
    }

}
