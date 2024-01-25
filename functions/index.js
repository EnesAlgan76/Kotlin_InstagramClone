const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();




exports.takipIstegiBildirimiGonder = functions.firestore
    .document('users/{userId}/notifications/{notificationId}')
    .onCreate(async (snapshot, context) => {

        const bilidirimSahibiId = context.params.userId

        console.log('NEW notification detected: ', bilidirimSahibiId);

        const notificationData = snapshot.data();
        const userId = context.params.userId;

        const type = notificationData.type;
        console.log('NEW notification type detected:', type);

        // Assign suitable text based on the notification type
        let title;
        switch (type) {
            case 'post_like':
                title = 'Post Liked';
                break;
            case 'follow_request':
                title = 'Follow Request';
                break;
            case 'comment':
                title = 'New Comment';
                break;
            default:
                title = 'Unknown Notification';
        }

        try {
            const userSnapshot = await admin.firestore().collection('users').doc(userId).get();
            const userData = userSnapshot.data();

            const fcmToken = userData.fcmToken;

            console.log('FCM TOKEN ---- >> ', fcmToken);

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
