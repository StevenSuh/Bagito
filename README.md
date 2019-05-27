## Instructions
To fully test the app and server locally, open the app on Android Studio emulator and start the server by following instructions in the /server.

Note: `/server/credentials.json` is a combination of keys from google API, mailjet API, and stripe API. To replicate the file for your usage, follow the steps below.

## Credentials Instructions
- Get `credentials.json` from [google API console](https://console.cloud.google.com/apis/credentials) by clicking `Create credentials`.
- Get mailjet API keys from [mailjet API keys](https://app.mailjet.com/account/api_keys). Add the keys as `MJ_APIKEY_PUBLIC` and `MJ_APIKEY_PRIVATE`.
- Get stripe API key from [stripe dashboard](https://dashboard.stripe.com/test/apikeys). Add the secret key as `STRIPE_APIKEY`.
- Place the file in `/server`.
<br>
- Apart from `credentials.json`, modify the `stripe_key` XML value in `/app/BagitoApp/java/res/values/string.xml` to the stripe public API key.
