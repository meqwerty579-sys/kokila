# Installation and Activation Guide

Once the Kokila Offline Neural TTS engine APK has been compiled, follow these instructions to install, activate, and test the engine on any Android physical device (API level 24+).

## Step 1: Install the APK on your device
You can install the compiled APK via command line using ADB, or copy it directly to your device storage.

Using ADB:
```bash
# Connect your physical device with USB Debugging enabled, then run:
adb install app/build/outputs/apk/release/app-release.apk
```

Using manual file transfer:
1.  Copy the `app-release.apk` to your phone's downloads directory.
2.  On your phone, open any File Manager app, locate the APK, and tap to install it.
3.  Ensure you grant permissions for "Unknown Sources" if prompted by your system.

---

## Step 2: Set Kokila as the Primary System TTS Engine
To route all system-wide synthesized speech through the offline engine:

1.  Open your device **Settings** app.
2.  Navigate to **System > Languages & Input > Text-to-speech output**.
    *(Note: On some manufacturers, this is under **Settings > Accessibility > Text-to-speech output**).*
3.  Tap on **Preferred engine**.
4.  You will see **Kokila Offline TTS** listed in the options.
5.  Select **Kokila Offline TTS**.
6.  The OS will display a standard warning: *"This speech synthesis engine may be able to collect all text that will be spoken..."*
7.  Tap **OK** to confirm. Since Kokila is completely offline and does not contain internet permissions, your data remains fully safe.

---

## Step 3: Test Local Neural Voice Output
1.  On the **Text-to-speech output** screen, locate the **Play** button.
2.  Tap **Play**. You will hear the real-time offline neural voice output!
3.  Adjust the **Speech rate** and **Pitch** sliders.
4.  Tap **Play** again to verify that speed and pitch variations are processed in real-time by the local synthesis layers.

---

## Step 4: Verify Local Performance Profile
1.  Launch the **Kokila TTS** app from your launcher screen.
2.  Observe the real-time systems dashboard.
3.  Tap **RUN SYSTEM DIAGNOSTICS** to execute a benchmark run of the ONNX inference engine.
4.  Verify that your local system metrics are healthy:
    *   **Inference Latency**: Under 30 ms (target is < 50 ms).
    *   **Real-Time Factor**: Under 0.25 rtf (target is < 0.5 rtf).
