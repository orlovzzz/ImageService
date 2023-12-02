package main

import (
	rpc "GoService/proto"
	"context"
	"fmt"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo/gridfs"
	"go.mongodb.org/mongo-driver/mongo/options"
	"google.golang.org/grpc"
	"io"
	"log"
	"net"
)

var bucket = ConfigDB()

type User struct {
	UserID int32 `bson:"user_id"`
}

type server struct {
	rpc.UnimplementedImageServiceServer
}

func (s *server) mustEmbedUnimplementedImageServiceServer() {
}

func (s *server) AddImage(ctx context.Context, request *rpc.ImageRequest) (*rpc.SuccessResponse, error) {
	user := User{
		UserID: request.UserID,
	}
	uploadStream, err := bucket.OpenUploadStream(request.Filename, options.GridFSUpload().SetMetadata(user))
	if err != nil {
		log.Println("Error open stream: ", err)
		return &rpc.SuccessResponse{Success: false}, nil
	}
	defer uploadStream.Close()
	_, err = uploadStream.Write(request.Image)
	if err != nil {
		log.Println("Error write file: ", err)
		return &rpc.SuccessResponse{Success: false}, nil
	}
	return &rpc.SuccessResponse{Success: true}, nil
}

func (s *server) GetAllImages(ctx context.Context, request *rpc.ImageRequest) (*rpc.ImageResponse, error) {
	cursor, err := bucket.Find(bson.M{"metadata.user_id": request.UserID})
	if err != nil {
		log.Println("Error find images with user id: ", err)
	}

	var images [][]byte
	var filenames []string
	for cursor.Next(context.Background()) {
		var file gridfs.File
		if err := cursor.Decode(&file); err != nil {
			log.Println("Error decode file: ", err)
		}

		filenames = append(filenames, file.Name)
		downloadStream, err := bucket.OpenDownloadStream(file.ID)
		if err != nil {
			log.Println("Error get images from file: ", err)
		}
		imageData, err := io.ReadAll(downloadStream)
		if err != nil {
			log.Println("Error read image: ", err)
		}
		images = append(images, imageData)
	}
	fmt.Printf("Found %d images with user_id = %d\n", len(images), request.UserID)
	return &rpc.ImageResponse{
		Image:    images,
		Filename: filenames,
	}, nil
}

func main() {
	lis, err := net.Listen("tcp", ":9091")
	if err != nil {
		log.Println("Error listen this port:", err)
	}
	s := grpc.NewServer()
	MyServer := &server{}
	rpc.RegisterImageServiceServer(s, MyServer)
	log.Printf("server listening at %v", lis.Addr())
	if err := s.Serve(lis); err != nil {
		log.Println("Error serve: ", err)
	}
}
