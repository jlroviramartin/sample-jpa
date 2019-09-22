package es.test.schedules;

import java.util.Locale;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        System.out.printf(new Locale("es", "ES"), "%tc Job trigged by Quartz...%n", new java.util.Date());
    }
}
