package fr.xpdustry.distributor.command.argument;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.caption.*;

import cloud.commandframework.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.*;
import java.util.function.*;


/**
 * A command argument for an online {@link Player}.
 *
 * @param <C> the command sender type
 */
public class PlayerArgument<C> extends CommandArgument<C, Player>{
    private PlayerArgument(
        final boolean required,
        final @NonNull String name,
        final @NonNull String defaultValue,
        final @Nullable BiFunction<@NonNull CommandContext<C>,
            @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider,
        final @NonNull ArgumentDescription defaultDescription
    ){
        super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> @NonNull Builder<C> newBuilder(final @NonNull String name){
        return new Builder<>(name);
    }

    /**
     * Create a new required {@link PlayerArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> @NonNull CommandArgument<C, Player> of(final @NonNull String name){
        return PlayerArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional {@link PlayerArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> @NonNull CommandArgument<C, Player> optional(final @NonNull String name){
        return PlayerArgument.<C>newBuilder(name).asOptional().build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Player>{
        private Builder(final @NonNull String name){
            super(Player.class, name);
        }

        /**
         * Build a new {@link PlayerArgument}.
         *
         * @return the constructed player argument
         */
        @Override
        public @NonNull PlayerArgument<C> build(){
            return new PlayerArgument<>(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }

    public static final class PlayerParser<C> implements ArgumentParser<C, Player>{
        @Override public @NonNull ArgumentParseResult<Player> parse(
            final @NonNull CommandContext<C> ctx,
            final @NonNull Queue<@NonNull String> inputQueue
        ){
            final var input = inputQueue.peek();
            if(input == null) return ArgumentParseResult.failure(new NoInputProvidedException(PlayerParser.class, ctx));

            final var player = Groups.player.find(p ->
                Strings.stripColors(p.name()).equalsIgnoreCase(input) || p.id() == Strings.parseInt(input)
            );

            if(player == null){
                return ArgumentParseResult.failure(new PlayerParseException(input, ctx));
            }else{
                inputQueue.remove();
                return ArgumentParseResult.success(player);
            }
        }

        @Override
        public boolean isContextFree(){
            return true;
        }
    }

    public static final class PlayerParseException extends ParserException{
        @Serial private static final long serialVersionUID = 3264229396134848993L;
        private final String input;

        /**
         * Create a new {@link PlayerParseException}.
         *
         * @param input the input string
         * @param ctx   the command context
         */
        public PlayerParseException(
            final @NonNull String input,
            final @NonNull CommandContext<?> ctx
        ){
            super(PlayerParser.class, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, CaptionVariable.of("input", input));
            this.input = input;
        }

        /** @return the input string */
        public @NonNull String getInput(){
            return this.input;
        }
    }
}
