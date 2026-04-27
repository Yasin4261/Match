package com.match;

import com.match.bootstrap.ApiApplication;
import org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Test-only application root in the {@code com.match} package so any
 * {@code @SpringBootTest} located under {@code com.match.*} can locate a
 * {@code @SpringBootConfiguration}.
 *
 * The production {@link ApiApplication} is excluded from component scanning to
 * prevent its own {@code @EnableAutoConfiguration} from re-introducing
 * autoconfigurations we explicitly exclude here (mail).
 */
@SpringBootApplication(
    scanBasePackages = "com.match",
    exclude = {
        MailSenderAutoConfiguration.class,
        MailHealthContributorAutoConfiguration.class
    }
)
@ComponentScan(
    basePackages = "com.match",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = ApiApplication.class
    )
)
public class IntegrationTestApplication { }




