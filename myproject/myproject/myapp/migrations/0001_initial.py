# Generated by Django 5.0 on 2023-12-12 17:59

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Chart',
            fields=[
                ('id', models.IntegerField(primary_key=True, serialize=False)),
                ('chart_name', models.CharField(max_length=255)),
                ('chart_image', models.ImageField(upload_to='chart_image/')),
            ],
        ),
    ]
