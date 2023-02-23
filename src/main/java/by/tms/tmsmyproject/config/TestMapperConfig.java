package by.tms.tmsmyproject.config;

import by.tms.tmsmyproject.entities.mapers.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({BookMapperImpl.class, AuthorMapperImpl.class, UserMapperImpl.class, ItemMapperImpl.class})
public class TestMapperConfig {
}
