package de.bushnaq.abdalla.family;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import de.bushnaq.abdalla.util.MavenPropertiesProvider;

@ComponentScan(basePackages = { "de.bushnaq.abdalla" })
@SpringBootApplication
public class Application implements CommandLineRunner {
	private static String	buildNumber		= MavenPropertiesProvider.getProperty(Application.class, "build.number");
	private static boolean	lazyStart		= true;																		// for junit tests
	private static String	moduleVersion	= MavenPropertiesProvider.getProperty(Application.class, "module.version");

	/**
	 * APPLICATION Called 1st when started as APPLICATION Not called when running junit test
	 *
	 */
	public static void main(String[] args) {
		lazyStart = false;
		SpringApplicationBuilder		springApplicationBuilder	= new SpringApplicationBuilder(Application.class);
		ConfigurableApplicationContext	context						= springApplicationBuilder.headless(false).run(args);
		context.close();
	}

	private final Logger	logger	= LoggerFactory.getLogger(this.getClass());

	@Autowired
	Main					main;

	public Application() {
	}

	/**
	 * UNIT TEST Called when running as application Called when running UNIT TEST
	 *
	 */
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) throws Exception {
		if (lazyStart)
			logger.info(String.format("starting family.tree %s.%s within a unit test", moduleVersion, buildNumber));
	}

	@Override
	public void run(String... args) throws Exception {
		if (!lazyStart) {
			logger.info(String.format("starting family.tree %s.%s from command promt", moduleVersion, buildNumber));
			main.start(args);
		}
	}

}
