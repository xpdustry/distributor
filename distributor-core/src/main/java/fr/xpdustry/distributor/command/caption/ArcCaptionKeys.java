package fr.xpdustry.distributor.command.caption;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public final class ArcCaptionKeys{
    private ArcCaptionKeys(){
    }

    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>();
    /** Variables: {input} */
    public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER = of("argument.parse.failure.player");
    /** Variables: {message} */
    public static final Caption ARGUMENT_PARSE_FAILURE = of("argument.parse.failure");
    /** Variables: {syntax} */
    public static final Caption COMMAND_INVALID_SYNTAX = of("command.invalid.syntax");
    /** Variables: {permission} */
    public static final Caption COMMAND_INVALID_PERMISSION = of("command.invalid.permission");
    /** Variables: {command} */
    public static final Caption COMMAND_FAILURE_NO_SUCH_COMMAND = of("command.failure.no_such_command");
    /** Variables: {message} */
    public static final Caption COMMAND_FAILURE_EXECUTION = of("command.failure.execution");
    /** Variables: {message} */
    public static final Caption COMMAND_FAILURE = of("command.failure");

    private static @NonNull Caption of(final @NonNull String key){
        final Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    public static @NonNull Collection<Caption> getArcCaptionKeys(){
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}
