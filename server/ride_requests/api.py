from django.contrib.auth.decorators import login_required
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from .models import RideRequest

@login_required
def request_ride(request):
    if request.method != 'POST':
        HttpResponseNotFound('Incorrect access method')

    dest_latitude = request.POST.get('destination_latitude')
    dest_longitude = request.POST.get('destination_longitude')
    if dest_latitude and destination_longitude:
        ride = RideRequest(user=request.user, destination_latitude=dest_latitude, destination_longitude=dest_longitude)
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
