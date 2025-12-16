package tech.challenge.scheduling.system.exceptions.handlers;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import tech.challenge.scheduling.system.exceptions.ResourceNotFoundException;
import java.time.format.DateTimeParseException;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex;
            String message = cve.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("VALIDATION ERROR");
                
            return GraphqlErrorBuilder.newError()
                .message("VALIDATION ERROR: " + message)
                .errorType(ErrorType.BAD_REQUEST)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError()
                .message("INVALID ARGUMENTS: " + ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        if (ex instanceof DateTimeParseException) {
            DateTimeParseException dtpe = (DateTimeParseException) ex;
            return GraphqlErrorBuilder.newError()
                .message("BAD DATE FORMAT ERROR: '" + dtpe.getParsedString() + "'. Use o formato: yyyy-MM-ddTHH:mm:ss")
                .errorType(ErrorType.BAD_REQUEST)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        if (ex instanceof ResourceNotFoundException) {
            return GraphqlErrorBuilder.newError()
                .message("RESOURCE NOT FOUND: " + ex.getMessage())
                .errorType(ErrorType.NOT_FOUND)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        if (ex instanceof NullPointerException) {
            return GraphqlErrorBuilder.newError()
                .message("NULL FIELD ERROR: " + ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        // 6. Outros Runtime Errors
        if (ex instanceof RuntimeException) {
            return GraphqlErrorBuilder.newError()
                .message("INTERNAL ERROR: " + ex.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR)
                .location(env.getField().getSourceLocation())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        
        // Deixa outros handlers tratarem
        return null;
    }
}