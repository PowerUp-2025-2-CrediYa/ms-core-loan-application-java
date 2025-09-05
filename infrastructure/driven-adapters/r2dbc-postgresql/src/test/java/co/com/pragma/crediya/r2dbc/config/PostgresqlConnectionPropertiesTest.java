package co.com.pragma.crediya.r2dbc.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresqlConnectionPropertiesTest {

    private static final String PREFIX = "adapters.r2dbc";

    private static ConfigurationPropertySource sourceOf(Map<String, String> entries) {
        return new MapConfigurationPropertySource(entries);
    }

    @Nested
    @DisplayName("Binding con prefijo 'adapters.r2dbc'")
    class BindingSuccess {

        @Test
        @DisplayName("Bindea correctamente todas las propiedades (conversión de tipos incluida)")
        void shouldBindAllProperties() {
            Map<String, String> props = Map.of(
                    PREFIX + ".host", "db.example.local",
                    PREFIX + ".port", "5432",
                    PREFIX + ".database", "loans",
                    PREFIX + ".schema", "public",
                    PREFIX + ".username", "app_user",
                    PREFIX + ".password", "s3cr3t"
            );

            Binder binder = new Binder(sourceOf(props));
            BindResult<PostgresqlConnectionProperties> result =
                    binder.bind(PREFIX, Bindable.of(PostgresqlConnectionProperties.class));

            assertThat(result.isBound()).isTrue();

            PostgresqlConnectionProperties cfg = result.get();
            assertThat(cfg.host()).isEqualTo("db.example.local");
            assertThat(cfg.port()).isEqualTo(5432);
            assertThat(cfg.database()).isEqualTo("loans");
            assertThat(cfg.schema()).isEqualTo("public");
            assertThat(cfg.username()).isEqualTo("app_user");
            assertThat(cfg.password()).isEqualTo("s3cr3t");
        }

        @Test
        @DisplayName("Campos faltantes → el Binder deja null para los componentes ausentes")
        void missingFieldsBecomeNull() {
            Map<String, String> props = Map.of(
                    PREFIX + ".host", "localhost",
                    PREFIX + ".port", "15432",
                    PREFIX + ".database", "loans"
            );

            Binder binder = new Binder(sourceOf(props));
            BindResult<PostgresqlConnectionProperties> result =
                    binder.bind(PREFIX, Bindable.of(PostgresqlConnectionProperties.class));

            assertThat(result.isBound()).isTrue();

            PostgresqlConnectionProperties cfg = result.get();
            assertThat(cfg.host()).isEqualTo("localhost");
            assertThat(cfg.port()).isEqualTo(15432);
            assertThat(cfg.database()).isEqualTo("loans");
            assertThat(cfg.schema()).isNull();
            assertThat(cfg.username()).isNull();
            assertThat(cfg.password()).isNull();
        }
    }

    @Nested
    @DisplayName("Binding con prefijo incorrecto")
    class BindingFailure {

        @Test
        @DisplayName("Prefijo distinto → no se bindea (isBound=false)")
        void wrongPrefixDoesNotBind() {
            Map<String, String> props = Map.of(
                    "adapters.other.host", "db.example.local",
                    "adapters.other.port", "5432"
            );

            Binder binder = new Binder(sourceOf(props));
            BindResult<PostgresqlConnectionProperties> result =
                    binder.bind(PREFIX, Bindable.of(PostgresqlConnectionProperties.class));

            assertThat(result.isBound()).isFalse();
        }
    }

    @Test
    @DisplayName("Semántica por valor (equals/hashCode) propia del record")
    void recordValueSemantics() {
        PostgresqlConnectionProperties a = new PostgresqlConnectionProperties(
                "h", 5432, "d", "s", "u", "p"
        );
        PostgresqlConnectionProperties b = new PostgresqlConnectionProperties(
                "h", 5432, "d", "s", "u", "p"
        );
        PostgresqlConnectionProperties c = new PostgresqlConnectionProperties(
                "h2", 5433, "d2", "s2", "u2", "p2"
        );

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
    }
}