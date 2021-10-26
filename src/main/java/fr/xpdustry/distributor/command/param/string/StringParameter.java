package fr.xpdustry.distributor.command.param.string;import fr.xpdustry.distributor.command.param.*;import io.leangen.geantyref.*;import org.jetbrains.annotations.*;public class StringParameter extends CommandParameter<String>{    public static final ArgumentPreprocessor<String> RAW_PREPROCESSOR = arg -> arg;    public StringParameter(@NotNull String name, @NotNull ArgumentPreprocessor<String> preprocessor,                           @NotNull String defaultValue, boolean optional, boolean variadic){        super(name, TypeToken.get(String.class), preprocessor, defaultValue, optional, variadic);    }    public StringParameter(@NotNull String name, @NotNull String defaultValue, boolean optional, boolean variadic){        this(name, RAW_PREPROCESSOR, defaultValue, optional, variadic);    }    public StringParameter(@NotNull String name){        super(name, TypeToken.get(String.class), RAW_PREPROCESSOR);    }}