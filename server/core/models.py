from django.db import models
from django.contrib.auth.models import AbstractUser

class User(AbstractUser):
    avatar = models.ImageField(upload_to='avatar')
    latitude = models.DecimalField(max_digits=7, decimal_places=5)
    longitude = models.DecimalField(max_digits=8, decimal_places=5)
    isDriver = models.BooleanField(default=False)
