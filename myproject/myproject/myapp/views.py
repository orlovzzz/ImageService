from django.http import HttpResponse, JsonResponse
import requests
import matplotlib.pyplot as plt
from io import BytesIO
from django.core.files.base import ContentFile
from .models import Chart

def save_to_database(name, image):
    new_chart = Chart(chart_name=name)
    new_chart.chart_image.save(f'{name}', image, save=True)

def endpoint(request):
    try:
        java_service_url = "http://client:8080/getData"
        response = requests.request('GET', java_service_url)

        if response.status_code == 200:
            chart = Chart.objects.filter(chart_name='Chart').first()
            if chart:
                print('Already exist')
                return HttpResponse(chart.chart_image, content_type="image/png")
            users = response.json()
            usernames = [item['username'] for item in users]
            img_count = [item['imgCount'] for item in users]
            plt.figure(figsize=(5, 5))
            plt.bar(usernames, img_count)
            plt.xlabel('Usernames')
            plt.ylabel('Image count')
            plt.title('Count of images')
            plt.xticks(rotation=45)
            plt.tight_layout()
            buffer = BytesIO()
            plt.savefig(buffer, format='png')
            plt.close()
            image = ContentFile(buffer.getvalue())
            save_to_database('Chart', image)
            return HttpResponse(image, content_type="image/png")
        else:
            return JsonResponse({"error": f"Error: {response.status_code} - {response.text}"}, status=500)

    except Exception as e:
        return JsonResponse({"error": f"Error: {str(e)}"}, status=500)