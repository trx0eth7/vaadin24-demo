package ru.trx.vaadindemo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

import static com.vaadin.flow.component.page.Viewport.DEFAULT;

@SpringBootApplication
@Push
@Viewport(DEFAULT)
public class VaadinDemoApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(VaadinDemoApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}
}
