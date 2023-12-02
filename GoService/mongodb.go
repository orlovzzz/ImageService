package main

import (
	"context"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/gridfs"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.mongodb.org/mongo-driver/mongo/readpref"
	"log"
)

func ConfigDB() gridfs.Bucket {
	client, err := mongo.Connect(context.TODO(), options.Client().ApplyURI("mongodb://mongo:27017"))
	if err != nil {
		log.Fatal("Error connect to database", err)
	}
	if err := client.Ping(context.TODO(), readpref.Primary()); err != nil {
		log.Fatal("Error ping database", err)
	}
	db := client.Database("rschir10")
	bucket, err := gridfs.NewBucket(db)
	if err != nil {
		log.Fatal("Error create new bucket", err)
	}
	return *bucket
}
