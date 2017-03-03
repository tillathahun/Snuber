from django.contrib.auth.decorators import login_required
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from django.contrib.auth import authenticate, login, logout
from .forms import UserForm
from django.middleware.csrf import get_token
import sys
sys.path.insert(0, '../ride_requests')

def register(request):
    if request.method != 'POST':
        return HttpResponseNotFound('Incorrect access method')

    form = UserForm(request.POST, request.FILES)
    print(request.POST)
    print(request.FILES)
    if form.is_valid():
        user = form.save()
        user.set_password(request.POST.get('password'))
        user.save()
        login(request, user)
        return JsonResponse({'success': True})

    print(form.errors)

    return JsonResponse({'success': False})


def auth_login(request):
    if request.method != 'POST':
        return HttpResponseNotFound('Incorrect access method')

    username = request.POST.get('username')
    password = request.POST.get('password')

    user = authenticate(username=username, password=password)
    if user is not None:
        login(request, user)
        return JsonResponse({'success': True, 'is_driver': user.isDriver})

    return JsonResponse({'success': False})

def csrf_token(request):
    token = get_token(request)
    return JsonResponse({'csrf_token': token})

@login_required
def auth_logout(request):
    logout(request)
    return JsonResponse({'success': True})

@login_required
def update_location(request):
    if request.method != 'POST':
        return HttpResponseNotFound('Incorrect access method')

    latitude = request.POST.get('latitude')
    longitude = request.POST.get('longitude')
    refresh_token = request.POST.get('refresh_token')

    if latitude:
        request.user.latitude = latitude

    if longitude:
        request.user.longitude = longitude

    if refresh_token:
        request.user.refresh_token = refresh_token

    request.user.save()

    update_ride(request)
    return JsonResponse({'success': True})

@login_required
def update_ride(request):
    if request.user.isDriver == True:
        ride = request.user.rides_requested.exclude(status__exact='CN').exclude(status__exact='CP').order_by('id')
        if ride.count() <= 0:
            return
        ride = ride[0]

        if ride.status == 'AC':
            ride.status = 'EN'
        elif ride.status == 'EN' and is_close(ride.driver.latitude, ride.driver.longitude, ride.user.latitude, ride.user.longitude):
            ride.status = 'IN'
            # url = 'https://fcm.googleapis.com/fcm/send'
            # headers = {'Content-Type': 'application/json', 'Authentication': 'key=AAAAPozWBDo:APA91bGK5ked7TUUA0N9NezzrygTVztROvjyizXfecztWsotDwRga1ZCbJ8YSVVidCSzKLeRNcp6fmBi4DUL3nAcf6zdampLAb2YdyFBn_WSVRvAXhN0JOjz9Q5n3huSqu4cMweoXBfE'}
            # payload = {'notification': {'title': 'Your SNAP driver has arrived', 'body': 'Tap to open map view'}, 'to': ride.user.refresh_token}
            # r = requests.post(url, headers=headers, json=payload)
            # print(r.json())
        elif ride.status == 'IN' and is_close(ride.driver.latitude, ride.driver.longitude, ride.destination_latitude, ride.destination_longitude):
            ride.status = 'CP'

def is_close(lat1, long1, lat2, long2):
    margin_of_error = 0.0003
    if abs(lat1 - lat2) <= margin_of_error and abs(long1 - long2) <= margin_of_error:
        return True

    return False
