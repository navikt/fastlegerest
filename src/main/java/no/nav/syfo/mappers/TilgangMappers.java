package no.nav.syfo.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.domain.Tilgang;
import org.springframework.http.ResponseEntity;

@Slf4j
public class TilgangMappers {
    public static Tilgang rs2Tilgang(ResponseEntity<String> response) {
        ObjectMapper mapper = new ObjectMapper();
        Tilgang tilgang;
        try {
            tilgang = mapper.readValue(response.getBody(), Tilgang.class);
        } catch (Exception e) {
            log.error("Fikk en exception ved lesing av json fra syfo-tilgangskontroll", e);
            throw new RuntimeException("Lesing av json fra syfo-tilgangskontroll feilet", e);
        }
        return tilgang;
    }
}
