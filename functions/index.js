const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.bildirimGonder = functions.firestore
    .document('notifications/{notificationId}')
    .onCreate(async (snapshot, context) => {

        const notificationId = context.params.notificationId;

        console.log('NEW notification detected with ID: ', notificationId);

        const notificationData = snapshot.data();

        const type = notificationData.type;
        const userName = notificationData.userName;
        const fcmToken = notificationData.fcmToken;

        console.log('NEW notification type detected:', type);

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
            const message = {
                notification: {
                    title: title,
                    body: messageText,
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
