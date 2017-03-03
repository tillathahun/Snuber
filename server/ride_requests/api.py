from django.contrib.auth.decorators import login_required
from django.contrib.auth import get_user_model
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from .models import RideRequest
from django.db.models import Count

@login_required
def request_ride(request):
    if request.method != 'POST':
        return HttpResponseNotFound('Incorrect access method')

    dest_latitude = request.POST.get('destination_latitude')
    dest_longitude = request.POST.get('destination_longitude')
    if dest_latitude and dest_longitude:
        user_model = get_user_model()
        driver = user_model.objects.filter(isDriver__exact=True).annotate(num_users=Count('rides_requested')).order_by('num_users')[0]
        ride = RideRequest(user=request.user, destination_latitude=dest_latitude, destination_longitude=dest_longitude, driver=driver, status='AC')
        ride.save()
        return JsonResponse({'success': True, 'ride_id': ride.id})
    return JsonResponse({'success': False})

@login_required
def cancel_ride_request(request, id):
    ride = get_object_or_404(RideRequest, pk=id)
    if ride.user == request.user:
        ride.status = 'CN'
        ride.save()
        return JsonResponse({'success': True})
    return JsonResponse({'success': False})

@login_required
def ride_details(request):
    if request.user.isDriver == True:
        if request.user.rides_requested.exclude(status__exact='CN').exclude(status__exact='CP').count() > 0:
            ride = request.user.rides_requested.exclude(status__exact='CN').exclude(status__exact='CP').order_by('id')[0]
            if ride.status == 'IP':
                latitude = ride.latitude
                longitude = ride.longitude
            else:
                latitude = ride.user.latitude
                longitude = ride.user.longitude

            response = {'is_queued': True, 'id': ride.id, 'user_name': ride.user.first_name + ride.user.last_name, 'user_image': ride.user.avatar.url, 'latitude': latitude, 'longitude': longitude, 'status': ride.status}
        else:
            response = {'is_queued': False}
        return JsonResponse({'success': True, 'ride': response})

    return JsonResponse({'success': False})

@login_required
def get_estimated_time(request):
    ride = request.user.ride_requests.exclude(status__exact='CN').exclude(status__exact='CP')
    if ride.count() > 0:
        ride = ride[0]
        driver = ride.driver
        queue = driver.rides_requested.exclude(status__exact='CN').exclude(status__exact='CP').order_by('id')
        pos = 0
        for ride_request in queue.all():
            pos += 1
            if ride_request.id == ride.id:
                break;

        return JsonResponse({'success': True, 'wait_time': pos * 7})
    return JsonResponse({'success': False})
