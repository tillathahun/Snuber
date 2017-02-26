from django.conf.urls import url, include

from . import api

urlpatterns = [
    url(r'^api/request/', api.request_ride, name='api_request_ride'),
    url(r'^api/cancel/', api.cancel_ride_request, name='api_cancel_ride')
]
