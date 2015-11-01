package jp.primecloud.auto.api;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import jp.primecloud.auto.api.response.ErrorResponse;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    private static Log log = LogFactory.getLog(ApiExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        String message;
        if (e instanceof AutoException || e instanceof AutoApplicationException) {
            message = e.getMessage();
        } else {
            message = MessageUtils.getMessage("EAPI-000000");
        }

        log.error(message, e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setSuccess(false);

        return Response.ok(errorResponse, MediaType.APPLICATION_JSON).build();
    }

}
