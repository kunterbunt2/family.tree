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

import de.bushnaq.abdalla.family.tree.ui.Launcher;
import de.bushnaq.abdalla.util.MavenProperiesProvider;

@ComponentScan(basePackages = { "de.bushnaq.abdalla" })
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static String startupMessage;
    private static boolean lazyStart = true;																		// for junit tests
    private static String svnRevision = MavenProperiesProvider.getProperty(Application.class, "svn.revision");
    private static String moduleName = MavenProperiesProvider.getProperty(Application.class, "module.name");
    private static String moduleVersion = MavenProperiesProvider.getProperty(Application.class, "module.version");
    private static String buildTime = MavenProperiesProvider.getProperty(Application.class, "build.time");

    /**
     * APPLICATION Called 1st when started as APPLICATION Not called when running junit test
     *
     */
    public static void main(String[] args) {
        startupMessage = String.format("starting %s %s-%s-%s as application", moduleName, moduleVersion, buildTime, svnRevision);
        lazyStart = false;
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Application.class);
        ConfigurableApplicationContext context = springApplicationBuilder.headless(false).run(args);
        context.close();
    }

    @Autowired
    Launcher launcher;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Main main;

    public Application() {
    }

    /**
     * UNIT TEST Called when running as application Called when running UNIT TEST
     *
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) throws Exception {
        if (lazyStart)
            startupMessage = String.format("starting %s %s-%s-%s within a unit test", moduleName, moduleVersion, buildTime, svnRevision);
        //            logger.info(String.format("starting family.tree %s.%s within a unit test", moduleVersion, buildNumber));
        logger.info("----------------------------------------------");
        logger.info(startupMessage);
        logger.info("----------------------------------------------");
    }

    @Override
    public void run(String... args) throws Exception {
        if (!lazyStart) {
            logger.info(String.format("executed %s %s-%s-%s", moduleName, moduleVersion, buildTime, svnRevision));
            //            logger.info(String.format("starting family.tree %s.%s from command promt", moduleVersion, buildNumber));
            if (args.length == 0) {
                logger.info(String.format("no arguments provided, starting user interface"));
                launcher.frmFamilytree.setVisible(true);
            } else
                main.start(args);
        }
    }

}
