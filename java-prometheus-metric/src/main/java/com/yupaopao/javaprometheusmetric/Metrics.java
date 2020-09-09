package com.yupaopao.javaprometheusmetric;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Metrics implements MeterBinder {
    private Counter counterOne;
    private Counter counterTwo;
    private AtomicInteger gaugeOne;
    private DistributionSummary summaryOne;
    private DistributionSummary summaryTwo;
    private DistributionSummary histogramOne;


    public Counter getCounterOne() {
        return counterOne;
    }

    public Counter getCounterTwo() {
        return counterTwo;
    }

    public AtomicInteger getGaugeOne() {
        return gaugeOne;
    }

    public DistributionSummary getSummaryOne() {
        return summaryOne;
    }

    public DistributionSummary getSummarTwo() {
        return summaryTwo;
    }

    public DistributionSummary getHistogramOne() {
        return histogramOne;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        //counter
        //Counter接口允许以固定的数值递增，该数值必须为正数。
        this.counterOne = Counter.builder("counter.one").tags(new String[]{"application","app1","method","get","url","/rules"}).description("This is the one counter").register(registry);
        this.counterTwo = (Counter) registry.counter("counter.two", "application","app1","method","get","url","/rules");

        // gauge
        this.gaugeOne = registry.gauge("gauge.one", Tags.concat(Tags.empty(),"application","app1","method","get","url","/rules"),new AtomicInteger(0));

       //summary
        this.summaryOne = registry.summary("summary.one","application","app1","method","get","url","/rules");

        this.summaryTwo = DistributionSummary.builder("summary.two")
                .description("simple distribution summary").tags("application","app1","method","get","url","/rules")
                .publishPercentiles(0.5, 0.75, 0.9)
                .register(registry);

        System.out.println(this.summaryTwo.takeSnapshot());

        // timer
        // 实际是gauge类型 和summary
        // 方法 record() 使用 Timer 对象的 record() 方法来记录一个 Runnable 对象的运行时间
        Timer timer = registry.timer("timer.simple.one","application","app1","method","get","url","/rules");
        timer.record(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Timer.Sample sample = Timer.start();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sample.stop(registry.timer("timer.sample","application","app1","method","get","url","/rules"));
        }).start();

        //histogram
        this.histogramOne = DistributionSummary.builder("histogram.one")
                .scale(1).tags(Tags.concat(Tags.empty(),"application","app1","method","get","url","/rules")).serviceLevelObjectives(70,80,90)
                .register(registry);

    }
}
