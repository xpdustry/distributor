package fr.xpdustry.distributor.command.exception;

import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;


/**
 * {@link CommandExceptionHandler} instances for exception handling in Distributor.
 */
public final class StandardExceptionHandlers{
    private StandardExceptionHandlers(){
    }

    /** Default exception handler for {@link InvalidSyntaxException}. */
    public static final CommandExceptionHandler<InvalidSyntaxException> COMMAND_INVALID_SYNTAX =
        CaptionExceptionHandler.ofSingle(ArcCaptionKeys.COMMAND_INVALID_SYNTAX, e -> CaptionVariable.of("syntax", e.getCorrectSyntax()));

    /** Default exception handler for {@link NoPermissionException}. */
    public static final CommandExceptionHandler<NoPermissionException> COMMAND_INVALID_PERMISSION =
        CaptionExceptionHandler.ofSingle(ArcCaptionKeys.COMMAND_INVALID_PERMISSION, e -> CaptionVariable.of("permission", e.getMissingPermission()));

    /** Default exception handler for {@link NoSuchCommandException}. */
    public static final CommandExceptionHandler<NoSuchCommandException> COMMAND_FAILURE_NO_SUCH_COMMAND =
        CaptionExceptionHandler.ofSingle(ArcCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND, e -> CaptionVariable.of("command", e.getSuppliedCommand()));

    /** Default exception handler for {@link ParserException}. */
    public static final CommandExceptionHandler<ParserException> ARGUMENT_PARSE_FAILURE =
        (s, e) -> s.send(MessageIntent.ERROR, e.errorCaption(), e.captionVariables());

    /** Default exception handler for {@link CommandExecutionException}. */
    public static final CommandExceptionHandler<CommandExecutionException> COMMAND_FAILURE_EXECUTION =
        CaptionExceptionHandler.ofSingle(ArcCaptionKeys.COMMAND_FAILURE_EXECUTION, e -> CaptionVariable.of("cause", e.getCause().getMessage()));

    /** Default exception handler for unexpected exceptions. */
    public static final CommandExceptionHandler<Throwable> COMMAND_FAILURE =
        CaptionExceptionHandler.ofSingle(ArcCaptionKeys.COMMAND_FAILURE, e -> CaptionVariable.of("message", e.getMessage()));
}
