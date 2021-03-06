from django.conf.urls import url

from . import api

urlpatterns = [
    url(r'^api/register/', api.register, name='api_register'),
    url(r'^api/login/', api.auth_login, name='api_login'),
    url(r'^api/logout/', api.auth_logout, name='api_logout'),
    url(r'^api/update-location/', api.update_location, name='api_update_location'),
    url(r'^api/csrf-token/', api.csrf_token, name='api_csrf_token')
]
