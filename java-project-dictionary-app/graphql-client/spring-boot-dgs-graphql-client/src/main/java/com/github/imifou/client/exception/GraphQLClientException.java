package com.github.imifou.client.exception;

import com.netflix.graphql.dgs.client.GraphQLError;
import lombok.Data;

import java.util.List;

@Data
public class GraphQLClientException extends RuntimeException {
    
    private final List<GraphQLError> errors;

    public GraphQLClientException(List<GraphQLError> errors) {
        super();
        this.errors = errors;
    }
}
