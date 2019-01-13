package com.ledahl.apps.recieppyapi.exception

import graphql.ErrorType
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.language.SourceLocation

class GraphQLErrorAdapter(private val error: GraphQLError): GraphQLError {
    override fun getMessage(): String {
        return when (error) {
            is ExceptionWhileDataFetching -> error.exception.message?.let { it } ?: "Unknown error"
            else -> error.message
        }
    }

    override fun getErrorType(): ErrorType {
        return error.errorType
    }

    override fun getLocations(): MutableList<SourceLocation> {
        return error.locations
    }
}