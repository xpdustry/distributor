// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.dependency;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class DependencyServiceTest {

    @Test
    void resolves_bound_instances_and_constructors() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(String.class, "hello");
            binder.bind(Integer.class, 42);
            binder.bind(TextAndNumber.class);
        });

        assertThat(dependencies.resolve(TextAndNumber.class)).isEqualTo(new TextAndNumber("hello", 42));
    }

    @Test
    void resolves_the_same_instance_every_time() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(String.class, "hello");
            binder.bind(Integer.class, 42);
            binder.bind(TextAndNumber.class);
        });

        assertThat(dependencies.resolve(TextAndNumber.class)).isSameAs(dependencies.resolve(TextAndNumber.class));
    }

    @Test
    void binds_an_interface_to_its_implementation() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(String.class, "hello");
            binder.bind(Greeting.class, GreetingImpl.class);
        });

        assertThat(dependencies.resolve(Greeting.class).text()).isEqualTo("hello");
    }

    @Test
    void later_bindings_override_earlier_ones() {
        final var dependencies = DependencyService.create(
                binder -> {
                    binder.bind(String.class, "hello");
                    binder.bind(Integer.class, 42);
                    binder.bind(TextAndNumber.class);
                },
                binder -> binder.bind(String.class, "bye"));

        assertThat(dependencies.resolve(TextAndNumber.class)).isEqualTo(new TextAndNumber("bye", 42));
    }

    @Test
    void resolves_all_the_instances_in_creation_order() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(Greeting.class, GreetingImpl.class);
            binder.bind(TextAndNumber.class);
            binder.bind(String.class, "hello");
            binder.bind(Integer.class, 42);
        });

        assertThat(dependencies.resolveAll())
                .containsExactly("hello", new GreetingImpl("hello"), 42, new TextAndNumber("hello", 42), dependencies);
    }

    @Test
    void instantiates_without_binding() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(String.class, "hello");
            binder.bind(Integer.class, 42);
        });

        assertThat(dependencies.instantiate(TextAndNumber.class)).isEqualTo(new TextAndNumber("hello", 42));
        assertThat(dependencies.resolveAll()).containsExactlyInAnyOrder("hello", 42, dependencies);
    }

    @Test
    void uses_an_annotated_secondary_constructor() {
        final var dependencies = DependencyService.create(binder -> binder.bind(String.class, "hello"));

        assertThat(dependencies.instantiate(SecondaryInjectClass.class))
                .isEqualTo(new SecondaryInjectClass("hello", -8));
    }

    @Test
    void rejects_classes_without_an_injectable_constructor() {
        assertThatThrownBy(() -> DependencyService.create().instantiate(NoInjectClass.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("has no injectable constructor");
    }

    @Test
    void rejects_circular_dependencies() {
        final var dependencies = DependencyService.create(binder -> {
            binder.bind(CircularA.class);
            binder.bind(CircularB.class);
        });

        assertThatThrownBy(() -> dependencies.resolve(CircularA.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Circular bindings detected");
    }

    @Test
    void rejects_generic_constructor_parameters() {
        final var dependencies = DependencyService.create(binder -> binder.bind(GenericParameterClass.class));

        assertThatThrownBy(() -> dependencies.resolve(GenericParameterClass.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Failed to create injection key for parameter")
                .hasRootCauseMessage("java.util.function.Supplier<java.lang.String> is a generic type");
    }

    @Test
    void rejects_generic_bindings() {
        assertThatThrownBy(() ->
                        DependencyService.create(binder -> binder.bind(GenericService.class, GenericServiceImpl.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("has generic type parameters");
    }

    record TextAndNumber(String text, Integer number) {
        @Inject
        TextAndNumber {}
    }

    interface Greeting {
        String text();
    }

    record GreetingImpl(String text) implements Greeting {
        @Inject
        GreetingImpl {}
    }

    record NoInjectClass(String text) {}

    record SecondaryInjectClass(String text, Integer number) {
        @Inject
        SecondaryInjectClass(final String text) {
            this(text, -8);
        }
    }

    record CircularA(CircularB dependency) {
        @Inject
        CircularA {}
    }

    record CircularB(CircularA dependency) {
        @Inject
        CircularB {}
    }

    static final class GenericParameterClass {

        @Inject
        GenericParameterClass(final Supplier<String> text) {}
    }

    interface GenericService<T> {}

    static final class GenericServiceImpl implements GenericService<String> {}
}
