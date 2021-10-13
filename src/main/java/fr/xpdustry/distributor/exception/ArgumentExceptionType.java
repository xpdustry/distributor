package fr.xpdustry.distributor.exception;

public enum ArgumentExceptionType{
    CUSTOM,
    UNDEFINED,

    // Basic
    ARGUMENT_NUMBER_TOO_BIG,
    ARGUMENT_NUMBER_TOO_LOW,

    NUMERIC_VALUE_TOO_BIG,
    NUMERIC_VALUE_TOO_LOW,

    ARGUMENT_FORMATTING_ERROR,

    UNKNOWN_PARAMETER_TYPE;
}
