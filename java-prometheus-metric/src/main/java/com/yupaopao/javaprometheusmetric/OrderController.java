package com.yupaopao.javaprometheusmetric;

import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//http://localhost:8080/actuator/prometheus
@RequestMapping("/")
@RestController
public class OrderController {
    @Autowired
    MeterRegistry registry;

    @Autowired
    private Metrics metrics;


    @RequestMapping("/test")
    public String zhuyu(){
        metrics.getCounterOne().increment();
        metrics.getCounterTwo().increment();

        metrics.getGaugeOne().set(2000);

        metrics.getSummaryOne().record(300);

        metrics.getSummarTwo().record(20);
        metrics.getSummarTwo().record(30);
        metrics.getSummarTwo().record(40);
        metrics.getSummarTwo().record(50);

        metrics.getHistogramOne().record(30);
        metrics.getHistogramOne().record(40);
        metrics.getHistogramOne().record(90);
        return "test";
    }

}
