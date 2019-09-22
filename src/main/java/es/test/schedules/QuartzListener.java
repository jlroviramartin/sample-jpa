package es.test.schedules;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// http://www.cronmaker.com/
// https://www.freeformatter.com/cron-expression-generator-quartz.html
// http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html
// https://www.javamexico.org/blogs/jpaul/ejemplo_basico_quartz_221_tomcat_7054_con_edicion_de_expresion_cron
@WebListener
public class QuartzListener extends QuartzInitializerListener {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);

        ServletContext ctx = sce.getServletContext();
        StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);
        try {
            Scheduler scheduler = factory.getScheduler();

            JobDetail job = JobBuilder.newJob(QuartzJob.class).build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("simple")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 * * ? * *")) // Every minute
                    .startNow()
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (Exception e) {
            LOG.error("Error while the job os created", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }
}
