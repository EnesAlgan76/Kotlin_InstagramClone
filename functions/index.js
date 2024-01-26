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
        const userName = notificationData.user_name; // Assuming user name is stored in 'user_name'
        console.log('NEW notification type detected:', type);

        // Assign suitable text based on the notification type
        let title, messageText;
        switch (type) {
            case 'post_like':
                title = 'Post Liked';
                messageText = `${userName} liked your post.`;
                break;
            case 'follow_request':
                title = 'Follow Request';
                messageText = `${userName} sent you a follow request.`;
                break;
            case 'comment':
                title = 'New Comment';
                messageText = `${userName} commented on your post.`;
                break;
            default:
                title = 'Unknown Notification';
                messageText = 'Unknown notification type.';
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
                    body: messageText, // Use the dynamically generated message
                },
                token: fcmToken,
                data: {
                        click_action: 'NOTIFICATION_CLICK',
                    },
            };

            await admin.messaging().send(message);
            console.log('Notification sent successfully');
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });

