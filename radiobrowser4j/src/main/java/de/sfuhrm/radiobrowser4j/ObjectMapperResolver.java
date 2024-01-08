package de.sfuhrm.radiobrowser4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

    /** The object mapper to use for mapping. */
    private final ObjectMapper mapper;

    /** Creates a new instance. */
    ObjectMapperResolver() {
        mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mapper.setDateFormat(df);
        Map<Class<?>, Class<?>> mixIns = new HashMap<>();
        mixIns.put(Station.class, StationMixin.class);
        mapper.setMixIns(mixIns);
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return mapper;
    }
}
