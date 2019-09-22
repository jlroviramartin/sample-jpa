package es.test.schedules;

import java.util.Locale;

public class ScheduledJob implements Runnable {

    @Override
    public void run() {
        System.out.printf(new Locale("es", "ES"), "%tc Job trigged by scheduler...%n", new java.util.Date());
    }
}
