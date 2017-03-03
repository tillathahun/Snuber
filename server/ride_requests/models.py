from django.db import models
from django.conf import settings

class RideRequest(models.Model):
    STATUS_CHOICES = (
        ('RQ', 'Requested'),
        ('AC', 'Accepted'),
        ('EN', 'En Route'),
        ('IP', 'Ride In Progress'),
        ('CP', 'Ride Completed'),
        ('CN', 'Ride Canceled'),
    )

    destination_latitude = models.DecimalField(max_digits=7, decimal_places=5)
    destination_longitude = models.DecimalField(max_digits=8, decimal_places=5)
    user = models.ForeignKey(settings.AUTH_USER_MODEL, related_name='ride_requests')
    driver = models.ForeignKey(settings.AUTH_USER_MODEL, related_name='rides_requested', blank=True)
    status = models.CharField(max_length=2, choices=STATUS_CHOICES, default='RQ')

    def __str__(self):
        return "Ride Request #" + self.id
