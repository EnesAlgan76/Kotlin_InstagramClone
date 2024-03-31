# Kotlin_InstagramClone

Instagram Clone created with Kotlin and Firebase.

## Backend Project

The backend of this Instagram clone application is developed separately with Spring Boot + AWS. You can find the backend project on GitHub:

[Java Spring Backend Project](https://github.com/EnesAlgan76/SpringBackend_InstagramClone)


⚠️ **Attention: Please note that there are two branches in this repository**

- `CompletedApp(Firebase)`: This branch contains the backend integrated with **Firebase**.
- `Spring-Backend-Integrated`: This branch contains the backend integrated with **Spring Java**.

## Spring Integration (AWS Deployment)
This branch integrates the backend with Spring Java, AWS services for database hosting and deployment.

### Technologies Used

- **Android Architecture Components**: Used for implementing the MVVM architecture pattern, including LiveData and ViewModel.
- **Hilt**:Dependency injection framework for Android.
- **Spring Boot**: Backend framework for building scalable and efficient Java applications.
- **MySQL**: Database used for storing user data, posts, comments, and more.
- **Amazon RDS**: Managed relational database service used for hosting the MySQL database.
- **Amazon Elastic Beanstalk**: Service used for deploying and managing the Spring Boot application.
- **Retrofit**: In Android used for making network requests to external APIs.
- **Kotlin Coroutine**: Utilized for handling asynchronous tasks.
- **Firebase**: Storage, Messaging, Cloud Functions

### AWS Deployment Scheme

<img src="https://github.com/EnesAlgan76/SpringBackend_InstagramClone/blob/master/images/overview.jpg" alt="AWS Deployment Scheme" height="400">


## Firebase Integration
This branch integrates the backend with Firebase.
 
### Technologies Used
- **Firebase Firestore**: Used as the database.
- **Firebase Storage**: Utilized for storing image and video files.
- **Firebase Authentication**: Employed for user authentication.
- **Firebase Messaging**: Used for sending real-time notifications.
- **Firebase Functions**: Used for managing backend logic.
- **Kotlin Coroutine**: Utilized for handling asynchronous tasks.







# Features


## Sign Up / Login

* Register using email or phone. For phone registration, receive a verification code. Firebase authentication used.
* On the profile page, users can change their profile picture, update other information, and adjust various settings.

<div style="display: flex; flex-direction: row;">
    <img src="gifs/register.gif" alt="SignUp" height="450" />
    <img src="gifs/profile.gif" alt="SignUp" height="450" />
</div>



## Follow Request
* In the search tab, users can dynamically search for desired individuals and send follow requests.
* On the backend side, we use Firebase functions to send real-time notifications to users.
* When a follow request is accepted, users' posts become visible and they can start messaging each other.
<img src="gifs/follow_request.gif" alt="SignUp" height="500" />


## Messaging
* Users can message each other in real-time.
<img src="gifs/chat.gif" alt="SignUp" height="500" />


## User Interactions
* Users can like and comment on posts of the accounts they follow, and they can even like comments. 
* These actions trigger Firebase functions backend code written in JavaScript, which in turn triggers Firebase messaging to send real-time notifications.
* Videos on the main page have autoplay feature.
<img src="gifs/comment.gif" alt="SignUp" height="500" />


## Share Post
* After permissions are granted, users can share photos and videos from their gallery or directly from their phone camera.
* Before being uploaded to Firebase Storage, images and videos are compressed.
<img src="gifs/share_post2.gif" alt="SignUp" height="500" />


## Share Story
* With the story feature, users can capture photos using both the front and rear cameras and share these photos with their followers as stories.
<img src="gifs/share_story.gif" alt="SignUp" height="500" />


## View Story
<img src="gifs/story_view.gif" alt="SignUp" height="500" />
