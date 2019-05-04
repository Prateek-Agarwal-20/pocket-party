const functions = require('firebase-functions');
const admin = require('firebase-admin');

const cors = require('cors')

admin.initializeApp();

exports.getCustomToken = functions.https.onRequest(async(req, res) => {
  const spotifyAccessToken = req.query.spotifyAccessToken;
  admin.auth().createCustomToken(spotifyAccessToken)
    .then(customToken => {
      console.log("CUSTOM TOKEN " + customToken)
      res.send(customToken)
      return
    })
    .catch({})
})
