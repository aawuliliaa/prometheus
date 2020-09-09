from django.http import HttpResponse
from django.views import View

from djangoPrometheusExporter import counter, gauge, histogram, summary


class Test(View):

    def get(self, request):
        counter.labels('app1', 'get', '/').inc(4)
        gauge.labels('app1', 'get', '/').set(40)
        histogram.labels('app1', 'get', '/').observe(40)
        histogram.labels('app1', 'get', '/').observe(50)
        histogram.labels('app1', 'get', '/').observe(60)
        summary.labels('app1', 'get', '/').observe(50)
        return HttpResponse(content='test')
