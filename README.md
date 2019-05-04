# pocket-party
Put the party in your pocket

# Start up
Make sure you have admin access to Firebase SDK in order to create custom log in tokens. To do this, you need a valid service account json file accessible through environment variables. The json can be stored with your clone of the project, but make sure it's in the .gitignore becuase it contains a secure RSA key that should not be leaked to the public. Currently, the .gitignore includes a line for "service-account-file.json" in the root of the project. You should store the json there under that name.

Once you have the file, if you are using a mac, run the following in your terminal: 
`export GOOGLE_APPLICATION_CREDENTIALS=<path-to-service-acount-json-file>`
This will last as long as your session lasts. If you start a new session, make sure to re run this terminal command.

