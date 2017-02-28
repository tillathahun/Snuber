from django.conf.urls import url

from . import api

urlpatterns = [
    url(r'^api/register/', api.register, name='api_register'),
    url(r'^api/login/', api.login, name='api_login'),
    url(r'^api/update-location', api.update_location, name='api_update_location')
]
