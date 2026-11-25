package com.app.hobbify;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HobbifyApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void startsWithInitialMigrationApplied() {
        Boolean migrationApplied = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM public.flyway_schema_history
                    WHERE version = '1' AND success
                )
                """, Boolean.class);

        assertThat(migrationApplied).isTrue();
    }
}
