package de.bushnaq.abdalla.family;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@ComponentScan(basePackages = { "com.ricoh.sdced" })
@SpringBootApplication
public class Application implements CommandLineRunner {
	private static boolean	lazyStart	= true;	// for junit tests
	private static boolean	started		= false;
	private static String	startupMessage;

	/**
	 * APPLICATION Called 1st when started as APPLICATION Not called when running junit test
	 *
	 */
	public static void main(String[] args) {
		SpringApplicationBuilder		springApplicationBuilder	= new SpringApplicationBuilder(Application.class);
		ConfigurableApplicationContext	context						= springApplicationBuilder.headless(false).run(args);
		context.close();
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Application() {
	}

	/**
	 * UNIT TEST Called when running as application Called when running UNIT TEST
	 *
	 */
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) throws Exception {
		logger.info("onAppöication");
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("run");
	}

}
