const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnNewNotification = functions.firestore
    .document('users/{userId}/notifications/{notificationId}')
    .onCreate(async (snapshot, context) => {
        const notificationData = snapshot.data();
        const userId = context.params.userId;

        const title = notificationData.title;
        // Extract other necessary data from notificationData

        try {
            const userSnapshot = await admin.firestore().collection('users').doc(userId).get();
            const userData = userSnapshot.data();

            const fcmToken = userData.fcmToken;

            // Send notification
            const message = {
                notification: {
                    title: title,
                    body: 'Your notification message here',
                },
                token: fcmToken,
            };

            await admin.messaging().send(message);
            console.log('Notification sent successfully');
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });
