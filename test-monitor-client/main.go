package main

import (
	"flag"
	"log"
	"net/http"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
	addr              = flag.String("listen-address", ":8080", "The address to listen on for HTTP requests.")
	uniformDomain     = flag.Float64("uniform.domain", 0.0002, "The domain for the uniform distribution.")
	normDomain        = flag.Float64("normal.domain", 10, "The domain for the normal distribution.")
	normMean          = flag.Float64("normal.mean", 70, "The mean for the normal distribution.")
	oscillationPeriod = flag.Duration("oscillation-period", 10*time.Minute, "The duration of the rate oscillation period.")
)

var (
	// 1. 创建 Prometheus 数据Metric, 就相当于SQL 数据库 声明table
	MyCount = prometheus.NewCounterVec(prometheus.CounterOpts{
		Name: "count_api",
		Help: "api请求次数",
	}, []string{"application", "method", "url"})

	MyGauge = prometheus.NewGaugeVec(prometheus.GaugeOpts{
		Name: "gauge_disk",
		Help: "磁盘使用量",
	}, []string{"application", "disk"})

	MySummary = prometheus.NewSummaryVec(
		prometheus.SummaryOpts{
			Name:       "summary_api_request_time",
			Help:       "api请求时间(单位是毫秒)",
			Objectives: map[float64]float64{0.3: 0.1, 0.4: 0.1, 0.5: 0.1, 0.6: 0.1, 0.7: 0.1, 0.8: 0.1, 0.9: 0.01},
		},
		[]string{"application", "method", "url"},
	)

	MyHistogram = prometheus.NewHistogram(prometheus.HistogramOpts{
		Name:        "histogram_api_request_time",
		Help:        "api请求时间(单位是毫秒)",
		ConstLabels: map[string]string{"application": "app1", "method": "get", "url": "/api/rules"},
		Buckets:     prometheus.LinearBuckets(*normMean, .5**normDomain, 10),
	})
)

func init() {
	// 2. 注册定义好的Metric 相当于执行SQL create table 语句

	prometheus.MustRegister(MyCount)
	prometheus.MustRegister(MyGauge)
	prometheus.MustRegister(MySummary)
	prometheus.MustRegister(MyHistogram)

	// Add Go module build info.
	prometheus.MustRegister(prometheus.NewBuildInfoCollector())
}

func main() {
	flag.Parse()
	go func() {
		for i := 1; i <= 3; i++ {
			MyCount.WithLabelValues("app1", "get", "/api/rules").Inc()
		}
	}()

	go func() {
		MyGauge.WithLabelValues("app1", "/disk1").Set(float64(12))
	}()

	go func() {
		numbers := []int{100, 90, 80, 70, 60, 60, 70}
		for _, num := range numbers {
			v := float64(num)
			MyHistogram.Observe(v)
			time.Sleep(2 * time.Second)
		}
	}()

	go func() {
		numbers := []int{100, 90, 80, 70, 60, 60, 70}
		for _, num := range numbers {
			v := float64(num)
			MySummary.WithLabelValues("app1", "get", "/api/rules").Observe(v)
			time.Sleep(2 * time.Second)
		}
	}()

	// 4. 提供HTTP API接口,让Prometheus 主程序定时来收集时序数据
	// Expose the registered metrics via HTTP.
	http.Handle("/metrics", promhttp.Handler())
	log.Fatal(http.ListenAndServe(*addr, nil))
}
