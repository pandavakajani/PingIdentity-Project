# PingIdentity-Project

I built the app using MVVM architecture and data binding.
The app has few major members:
  1. MainViewModel is the main logic class that handle UI events and is responsible for creating a future task and triggers the encrypt/decrypt/sign/validate
  2. FireBaseMessagingServiceImpl is responsible for digesting the incoming messages from firebase. it will trigger a notification if needed.
  3. EncryptionManager is responsible to wrap all the actions related to encryption/decryption etc.. it knows to save the RSA key inside the keyStore and operates        in the background for its calculations and key generations. it holds two members EncryptionKey and SigningKey that are responsible for doing the actions that      relate to encryption/decryption and signing/verifying
  
  The flow is as follows:
  1. Launching the app with MainFragment and initializing the model and viewModel to default values.
  2. Typing into EditText field and pressing the 'Send' button.
  3. The viewModel initiates a process in EncryptionManager to encryp and sign the text
  4. A task is created with a 15 seconds delay.
  5. When the app goes down to background (onStop), it will check a flag for sending the task and send it if needed
  6. After 15 seconds, the task is executed and a push message is sent
  7. The push msg is received in FireBaseMessagingServiceImpl and from there a notification is sent to the device
  8. When the user clicks on the notification msg, a pending intent holding the relevant data (signature and encrypted) is caught and handled inside MainActivity
  9. MainActivity triggers a navigation action to the DecryptedFragment and sends the data from the notification using SafeArgs.
  10. If needed, biometric authentication is invoked here. else -> go to next step directly.
  10. Now the fragment is using MainViewModel methods to invoke EncryptionManager to decrypt and verify the data 
  11. The background thread is updating the UI along the way in its progress.
  12. If all passes correctly, the MainModelView updates the relevant field in Model that is binded to the UI
  
  A few points i wanted to raise:
  1. The Task is created every time the user presses the 'Send' button only because i wanted to follow the instructions as written. If it were up to me, i would not      have created it this way since it's a waste of resources. initially i created the task only after going down to the background.
  2. I added a wait() after the 'Send' is pushed, since the process is done very quickly and the UI changes are not visible. Also the labels are seen as updated          simultaniously but in fact they are done sequencially.
  3. As a side note - I actually had a good time doing this project (not kidding). It made me learn more about encryption and key generating, and I enjoyed the          challenge.  
  
  
