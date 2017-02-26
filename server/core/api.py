from django.contrib.auth.decorators import login_required
from django.http import HttpResponseNotFound, JsonResponse
from django.shortcuts import get_object_or_404
from django.contrib.auth import authenticate, login
from .forms import UserForm

def register(request):
    if request.method != 'POST':
        HttpResponseNotFound('Incorrect access method')

    form = UserForm(request.POST, request.FILES)
    if form.is_valid():
        form.save()
        return JsonResponse({'success': True})

    return JsonResponse({'success': False})


def login(request):
    if request.method != 'POST':
        HttpResponseNotFound('Incorrect access method')

    username = request.POST.get('username')
    password = request.POST.get('password')
    user = authenticate(username=username, password=password)
    if user is not None:
        login(request, user)
        return JsonResponse({'success': True})

    return JsonResponse({'success': False})
