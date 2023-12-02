FROM golang:1.19

WORKDIR /app

COPY GoService/ .
RUN go mod download

RUN CGO_ENABLED=0 GOOS=linux go build -o /a

CMD ["/a"]