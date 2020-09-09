from prometheus_client import Counter, Histogram, Gauge, Summary

counter = Counter('python_counter_one', 'url请求总数', ['application', 'method', 'endpoint'])
gauge = Gauge('python_gauge_one', 'url请求时间', ['application', 'method', 'endpoint'])

histogram = Histogram('python_histogram_one', 'url请求时间分布',
                      ['application', 'method', 'endpoint'], buckets=[0, 50, 60, 70])
summary = Summary('python_summary_one', 'url请求时间分布', ['application', 'method', 'endpoint'])
