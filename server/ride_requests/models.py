from django.db import models
from django.contrib.auth import get_user_model

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
    user = models.ForeignKey(get_user_model(), related_name='ride_requests')
    driver = models.ForeignKey(get_user_model(), related_name='rides_requested', blank=True)
    status = models.CharField(max_length=2, choices=STATUS_CHOICES, default='RQ')
