# faceLibrary
Face similarity checking android App 
This is demo App for face similarity checking. 


The challenge is :

The objective is creation of a library to be integrated in a native Android app.

This library  must receive two face bitmap images, convert them to base64 string and get a similarity score of by invoking the YooniK.Face API.

After getting the similarity score, the method should apply a threshold to validate if the similarity score is above a configurable threshold (use 0.4 as default) to check if the two faces images are from the same person. The method should return True or False according to the validation check.

The rest api request has the following format:
{
"first_image": base64 string,
"second_image": base64 string, 
}

Android Screenshots 
https://user-images.githubusercontent.com/54929555/153846651-96d79a71-37b2-4344-9073-d3cee59fa76b.mp4
