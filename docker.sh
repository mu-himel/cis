#!/bin/bash
image=devopsaes/cpvms-be:newdev-1.0.11
docker build -t $image --no-cache .
echo  "image $image is built"
docker push $image
echo "image $image pushed"