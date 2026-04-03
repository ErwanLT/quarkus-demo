package fr.eletutour.exception.mapper;

import fr.eletutour.exception.business.TavernNotFoundException;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TavernNotFoundExceptionMapper extends AbstractExceptionMapper<TavernNotFoundException> {

    @Override
    protected int status(TavernNotFoundException ex) {
        return ex.getStatus();
    }

    @Override
    protected String title(TavernNotFoundException ex) {
        return ex.getTitle();
    }
}
