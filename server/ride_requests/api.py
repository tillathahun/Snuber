from django.contrib.auth.decorators import login_required
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from .models import RideRequest

@login_required
def cancel_ride_request(request, id):
    ride = get_object_or_404(RideRequest, pk=id)
    if ride.user = request.user:
        ride.status = 'CN'
        ride.save()
        return JsonResponse({'success': True})
    return JsonResponse({'success': False})
