from django.db import models

class Chart(models.Model):
    chart_name = models.CharField(max_length=255)
    chart_image = models.ImageField(upload_to='chart_image/')