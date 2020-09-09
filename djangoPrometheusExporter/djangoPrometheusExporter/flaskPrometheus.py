# !/usr/bin/env python
# -*- coding:utf-8 -*-
from flask import Flask, Response
from prometheus_client import Counter, Gauge, Histogram, Summary, \
    generate_latest, CollectorRegistry

app = Flask(__name__)
registry = CollectorRegistry()
counter = Counter('flask_counter_one', 'url请求总数', ['application', 'endpoint', 'method'], registry=registry)
gauge = Gauge('flask_gauge_one', 'url请求总数', ['application', 'endpoint', 'method'], registry=registry)
buckets = (50, 60, 70, float('inf'))
histogram = Histogram('my_histogram', 'url请求时间分布',
                      ['application', 'endpoint', 'method'], registry=registry, buckets=buckets)
summary = Summary('my_summary', 'url请求时间分布', ['application', 'endpoint', 'method'], registry=registry)


@app.route('/metrics')
def metric():
    return Response(generate_latest(registry), mimetype='text/plain')


@app.route('/test')
def test():
    counter.labels('app1', 'get', '/').inc(4)

    gauge.labels('app1', 'get', '/').set(40)

    histogram.labels('app1', 'get', '/').observe(40)
    histogram.labels('app1', 'get', '/').observe(50)
    histogram.labels('app1', 'get', '/').observe(60)

    summary.labels('app1', 'get', '/').observe(50)
    return Response("test", mimetype='text/plain')


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080)
