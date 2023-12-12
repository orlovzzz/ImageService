FROM python:3.8

ENV PYTHONUNBUFFERED 1

RUN mkdir /app

WORKDIR /app

COPY myproject/myproject/requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN python myproject/myproject/manage.py makemigrations
RUN python myproject/myproject/manage.py migrate
RUN python myproject/myproject/manage.py collectstatic --noinput

CMD ["python", "myproject/myproject/manage.py", "runserver", "localhost:80"]