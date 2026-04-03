package fr.eletutour.exception.mapper;

import fr.eletutour.exception.business.TavernAlreadyExistsException;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TavernAlreadyExistsExceptionMapper extends AbstractExceptionMapper<TavernAlreadyExistsException> {

    @Override
    protected int status(TavernAlreadyExistsException ex) {
        return ex.getStatus();
    }

    @Override
    protected String title(TavernAlreadyExistsException ex) {
        return ex.getTitle();
    }
}
