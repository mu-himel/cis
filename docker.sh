#!/bin/bash
image=devopsaes/cpvms-be:newdev-1.1.19
docker build -t $image --no-cache .
echo  "image $image is built"
docker push $image
echo "image $image pushed"