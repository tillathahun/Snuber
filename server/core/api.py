from django.contrib.auth.decorators import login_required
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from django.contrib.auth import authenticate, login, logout
from .forms import UserForm
from django.middleware.csrf import get_token
from ride_requests.api import update_ride

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
