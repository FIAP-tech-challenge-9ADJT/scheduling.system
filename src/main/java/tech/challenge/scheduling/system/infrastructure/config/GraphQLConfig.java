package tech.challenge.scheduling.system.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;

@Configuration
public class GraphQLConfig {

    @Bean
    public DataFetcherExceptionResolverAdapter exceptionResolver() {
        return new DataFetcherExceptionResolverAdapter() {
            @Override
            protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
                if (ex instanceof ConstraintViolationException) {
                    return GraphqlErrorBuilder.newError()
                        .message("Validation failed: " + ex.getMessage())
                        .errorType(ErrorType.BAD_REQUEST)
                        .location(env.getField().getSourceLocation())
                        .path(env.getExecutionStepInfo().getPath())
                        .build();
                } else if (ex instanceof IllegalArgumentException) {
                    return GraphqlErrorBuilder.newError()
                        .message("Invalid argument: " + ex.getMessage())
                        .errorType(ErrorType.BAD_REQUEST)
                        .location(env.getField().getSourceLocation())
                        .path(env.getExecutionStepInfo().getPath())
                        .build();
                }
                return null;
            }
        };
    }
}