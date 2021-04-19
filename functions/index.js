const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.addMessage = functions.https.onCall((data, context) => {
  const payload = {
    notification: {
      title: "Kicked out",
      body: "You were kicked out by a Trojan Check In/Out manager",
    },
  };

  // Send notifications to token.
  admin.messaging().sendToDevice(data.token, payload);
});
