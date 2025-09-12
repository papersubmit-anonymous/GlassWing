# Ground Truth APK List

| Index | Package Name   | URL                                                    |
| :---- | :------------- | ------------------------------------------------------ |
| 1     | Telsavideo     | https://github.com/telsacoin/telsavideo                |
| 2     | Amiibo network | https://github.com/EdwynZN/amiibo_network              |
| 3     | Authpass       | https://github.com/authpass/authpass                   |
| 4     | Immich         | https://github.com/immich-app/immich                   |
| 5     | Bluebubbles    | https://github.com/BlueBubblesApp/bluebubbles-app/     |
| 6     | LibreTrack     | https://github.com/proninyaroslav/libretrack           |
| 7     | Osram          | https://github.com/talkingpanda0/osram-remote          |
| 8     | Kalium         | https://github.com/BananoCoin/kalium_wallet_flutter    |
| 9     | Natrium        | https://github.com/appditto/natrium_wallet_flutter     |
| 10    | Blink Comp     | https://github.com/proninyaroslav/blink-comparison     |
| 11    | Weight Tracker | https://github.com/MSzalek-Mobile/weight_tracker       |
| 12    | Light Wallet   | https://github.com/JoinSEEDS/seeds_light_wallet        |
| 13    | Jidoujishou    | https://github.com/arianneorpilla/jidoujisho           |
| 14    | Meditation     | https://github.com/nyxkn/meditation                    |
| 15    | Yubico         | https://github.com/Yubico/yubioath-flutter             |
| 16    | ServerBox      | https://github.com/lollipopkit/flutter_server_box      |
| 17    | ESSE           | https://github.com/CympleTech/ESSE                     |
| 18    | Vidar          | https://github.com/DrSolidDevil/Vidar                  |
| 19    | Voiceliner     | https://github.com/maxkrieger/voiceliner               |
| 20    | Cake Wallet    | https://github.com/cake-tech/cake_wallet               |
| 21    | Squawker       | https://github.com/j-fbriere/squawker                  |
| 22    | Airdash        | https://github.com/simonbengtsson/airdash              |
| 23    | Trireme        | https://github.com/teal77/trireme                      |
| 24    | Timy           | https://github.com/leastauthority/destimy              |
| 25    | ShockAlarm     | https://github.com/computerelite/shockalarmapp         |
| 26    | Hacki          | https://github.com/Livinglist/Hacki                    |
| 27    | Thingsboard    | https://github.com/thingsboard/flutter_thingsboard_app |
| 28    | Lighthouse     | https://github.com/jeroen1602/lighthouse_pm            |
| 29    | Group-track    | https://github.com/canopas/group-track-flutter         |
| 30    | Piggyvault     | https://github.com/piggyvault/piggyvault               |

# Leak insertion details

## 1.Telsavideo

- Channel: telsa.dtok/ipfs

  - Source: android.telephony.TelephonyManager: java.lang.String getSubscriberId()

  - Sink: android.content.Intent: android.content.Intent setAction(java.lang.String)

## 2.Amiibo network

- Channel: com.dartz.amiibo_network/info_package
  - Source: android.location.Location: double getLongitude()
  - Sink: android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)
- Channel: com.dartz.amiibo_network/notification
  - Source: android.accounts.AccountManager: android.accounts.Account[] getAccounts()
  - Sink: java.io.OutputStream: void write(byte[])

## 3. Authpss

- Channel: app.authpass/misc
  - Source: android.telephony.TelephonyManager: java.lang.String getDeviceId()
  - Sink:android.util.Log: int e(java.lang.String,java.lang.String)

## 4.Immich

- Channel: immich/foregroundChannel
  - Source: android.bluetooth.BluetoothAdapter: java.lang.String getAddress()
  - Sink: android.util.Log: int e(java.lang.String,java.lang.String)
- Channel: immich/backgroundChannel
  - Source: android.net.wifi.WifiInfo: java.lang.String getMacAddress()
  - Sink: android.util.Log: int i(java.lang.String,java.lang.String)
- Channel: file_trash
  - Source: android.net.wifi.WifiInfo: java.lang.String getSSID()
  - Sink: android.util.Log: int w(java.lang.String,java.lang.String)
- Channel: immich/httpSSLOptions
  - Source: android.telephony.TelephonyManager: java.lang.String getLine1Number()
  - Sink: android.util.Log: int v(java.lang.String,java.lang.String)

## 5.Bluebubbles

- Channel: com.bluebubbles.messaging
  - Source:  android.telephony.TelephonyManager: java.lang.String getSimSerialNumber()
  - Sink:  android.util.Log: int i(java.lang.String,java.lang.String)

## 6.LibreTrack

- Channel: org.proninyaroslav.libretrack.barcode_scanner
  - Source:  android.telephony.TelephonyManager: java.lang.String getSubscriberId()
  - Sink:  android.util.Log: int v(java.lang.String,java.lang.String)
- Channel: org.proninyaroslav.libretrack.system_tray
  - Source: android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink: android.util.Log: int w(java.lang.String,java.lang.String)

## 7.Osram

- Channel: org.talkingpanda/irtransmitte

  - Source: java.util.Calendar: java.util.TimeZone getTimeZone()

  - Sink:android.content.Intent: android.content.Intent setClassName(android.content.Context,java.lang.String)

## 8.Kalium

- Channel: fappchannel

  - Source:  java.io.File: java.io.File getAbsoluteFile()

  - Sink:  java.io.OutputStream: void write(byte[],int,int)

## 9.Natrium

- Channel: fappchannel

  - Source:  java.io.File: java.io.File getCanonicalFile()

  - Sink:  java.io.Writer: void write(char[])

## 10.Blink Comp

- Channel:org.proninyaroslav.blink_comparison/save_ref_image_service
  - Source:java.util.Locale: java.lang.String getCountry()
  - Sink:java.io.Writer: void write(java.lang.String)
- Channel:org.proninyaroslav.blink_comparison/window_manager
  - Source:android.accounts.AccountManager: android.accounts.Account[] getAccounts()
  - Sink:android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)

## 11.Weight Tracker

- Channel: app.channel.shared.data

  - Source:  android.bluetooth.BluetoothAdapter: java.lang.String getAddress()

  - Sink:android.util.Log: int d(java.lang.String,java.lang.String)

## 12.Light Wallet

- Channel: lw.web_view.clear
  - Source:  android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink:android.util.Log: int d(java.lang.String,java.lang.String,java.lang.Throwable)

## 13.Jidoujishou

- Channel: com.pichillilorenzo/flutter_inappwebview_platformutil
  - Source: android.telephony.TelephonyManager: java.lang.String getDeviceId()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: com.pichillilorenzo/flutter_inappwebview_cookiemanager
  - Source: android.location.Location: double getLongitude()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: com.pichillilorenzo/flutter_inappwebview_android_serviceworkercontroller
  - Source: android.accounts.AccountManager: android.accounts.Account[] getAccounts()
  - Sink:android.util.Log: int w(java.lang.String,java.lang.String)
- Channel: com.pichillilorenzo/flutter_inappwebview_android_webviewfeature
  - Source: java.io.File: java.io.File getCanonicalFile()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: com.pichillilorenzo/flutter_inappbrowser
  - Source: android.bluetooth.BluetoothAdapter: java.lang.String getAddress()
  - Sink:android.telephony.SmsManager: void sendMultipartTextMessage(java.lang.String,java.lang.String,java.util.ArrayList,java.util.ArrayList,java.util.ArrayList)
- Channel: com.pichillilorenzo/flutter_inappwebview_static
  - Source: android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink:ndroid.telephony.SmsManager: void sendDataMessage(java.lang.String,java.lang.String,short,byte[],android.app.PendingIntent,android.app.PendingIntent)
- Channel: com.pichillilorenzo/flutter_headless_inappwebview
  - Source: android.telephony.gsm.GsmCellLocation: int getLac()
  - Sink:android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)
- Channel: com.pichillilorenzo/flutter_inappwebview_credential_database
  - Source: android.location.Location: double getLongitude()
  - Sink: android.util.Log: int d(java.lang.String,java.lang.String)
- Channel: com.pichillilorenzo/flutter_chromesafaribrowser
  - Source: android.net.wifi.WifiInfo: java.lang.String getSSID()
  - Sink:android.util.Log: int i(java.lang.String,java.lang.String)
- Channel: com.pichillilorenzo/flutter_inappwebview_static
  - Source: android.net.wifi.WifiInfo: java.lang.String getMacAddress()
  - Sink:android.util.Log: int e(java.lang.String,java.lang.String)

## 14.Meditation

- Channel: com.nyxkn.meditation/channelHelper

  - Source:  android.telephony.gsm.GsmCellLocation: int getLac()

  - Sink:android.util.Log: int e(java.lang.String,java.lang.String)

## 15.Yubico

- Channel: app.methods
  - Source: android.telephony.TelephonyManager: java.lang.String getDeviceId()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: android.log.redirect
  - Source: android.location.Location: double getLongitude()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: android.oath.methods
  - Source: android.accounts.AccountManager: android.accounts.Account[] getAccounts()
  - Sink:android.util.Log: int w(java.lang.String,java.lang.String)
- Channel: android.fido.methods
  - Source: java.io.File: java.io.File getCanonicalFile()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: android.management.methods
  - Source: android.bluetooth.BluetoothAdapter: java.lang.String getAddress()
  - Sink:android.telephony.SmsManager: void sendMultipartTextMessage(java.lang.String,java.lang.String,java.util.ArrayList,java.util.ArrayList,java.util.ArrayList)
- Channel: android.state.appContext
  - Source: android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink:ndroid.telephony.SmsManager: void sendDataMessage(java.lang.String,java.lang.String,short,byte[],android.app.PendingIntent,android.app.PendingIntent)
- Channel: com.yubico.authenticator.channel.nfc_overlay
  - Source: android.telephony.gsm.GsmCellLocation: int getLac()
  - Sink:android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)
- Channel: app.link.methods
  - Source: android.location.Location: double getLongitude()
  - Sink: android.util.Log: int d(java.lang.String,java.lang.String)
- Channel: qrscanner_zxing
  - Source: android.net.wifi.WifiInfo: java.lang.String getSSID()
  - Sink:android.util.Log: int i(java.lang.String,java.lang.String)
- Channel: com.yubico.authenticator.flutter_plugins.qr_scanner_channel
  - Source: android.net.wifi.WifiInfo: java.lang.String getMacAddress()
  - Sink:android.util.Log: int e(java.lang.String,java.lang.String)

- Channel: android.devices.deviceInfo
  - Source: java.io.File: java.io.File getAbsoluteFile()
  - Sink: android.util.Log: int wtf(java.lang.String,java.lang.String,java.lang.Throwable)
- Channel: android.oath.sessionState
  - Source: android.bluetooth.BluetoothAdapter: java.lang.String getAddress()
  - Sink: android.util.Log: int i(java.lang.String,java.lang.String)
- Channel: android.oath.credentials
  - Source: android.telephony.gsm.GsmCellLocation: int getLac()
  - Sink: android.util.Log: int w(java.lang.String,java.lang.String)
- Channel: android.fido.sessionState
  - Source: android.telephony.TelephonyManager: java.lang.String getDeviceId()
  - Sink:  java.io.OutputStream: void write(byte[],int,int)
- Channel: android.fido.credentials
  - Source: android.location.LocationManager: android.location.Location getLastKnownLocation(java.lang.String)
  - Sink:  java.io.OutputStream: void write(byte[])
- Channel: android.fido.fingerprints
  - Source: android.net.wifi.WifiInfo: java.lang.String getSSID()
  - Sink:  java.io.OutputStream: void write(byte[],int,int)
- Channel: android.fido.reset
  - Source: android.net.wifi.WifiInfo: java.lang.String getMacAddress()
  - Sink: android.os.Handler: boolean sendMessage(android.os.Message)
- Channel: android.fido.registerFp
  - Source: android.location.Location: double getLongitude()
  - Sink: java.io.Writer: void write(char[])

## 16.ServerBox

- Channel: tech.lolli.toolbox/main_chan

  - Source:  android.location.Location: double getLongitude()

  - Sink:android.util.Log: int v(java.lang.String,java.lang.String)

## 17.ESSE

- Channel: esse_core
  - Source:  android.location.LocationManager: android.location.Location getLastKnownLocation(java.lang.String)
  - Sink:android.util.Log: int w(java.lang.String,java.lang.String)

## 18.Vidar

- Channel: vidar_sms_helper
  - Source:  android.net.wifi.WifiInfo: java.lang.String getMacAddress()
  - Sink:android.util.Log: int wtf(java.lang.String,java.lang.String,java.lang.Throwable)
- Channel: vidar_sms_notifier
  - Source:  android.net.wifi.WifiInfo: java.lang.String getSSID()
  - Sink:com.example.leaktest.MainActivity: void startActivities(android.content.Intent[])

## 19.Voiceliner

- Channel: voiceoutliner.saga.chat/androidtx
  - Source:  android.telephony.TelephonyManager: java.lang.String getDeviceId()
  - Sink:com.example.leaktest.MainActivity: void startActivityForResult(android.content.Intent,int)

## 20.Cake Wallet

- Channel: com.cake_wallet/native_utils
  - Source:  android.location.LocationManager: android.location.Location getLastKnownLocation(java.lang.String)
  - Sink:android.util.Log: int v(java.lang.String,java.lang.String)
- Channel: cw_shared_external
  - Source:  android.location.Location: double getLatitude()
  - Sink:android.util.Log: int w(java.lang.String,java.lang.String)
- Channel: cw_mweb
  - Source:  android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink:android.util.Log: int d(java.lang.String,java.lang.String)
- Channel: cw_decred
  - Source:  java.util.Locale: java.lang.String getCountry()
  - Sink:android.util.Log: int i(java.lang.String,java.lang.String)

## 21.Squawker

- Channel: squawker/android_info
  - Source:  android.telephony.TelephonyManager: java.lang.String getLine1Number()
  - Sink:com.example.leaktest.MainActivity: void startActivityFromChild(android.app.Activity,android.content.Intent,int)

## 22.Airdash 

- Channel: io.flown.airdash/communicator

  - Source: android.telephony.TelephonyManager: java.lang.String getDeviceId()

  - Sink:android.util.Log: int e(java.lang.String,java.lang.String)

- io.flown.airdash/event_communicator

  - Source: android.database.Cursor: java.lang.String getString(int)
  - Sink: java.io.OutputStream: void write(byte[],int,int)

## 23.Trireme

- Channel: trireme_native_bridge
  - Source: android.telephony.TelephonyManager: java.lang.String getSimSerialNumber()
  - Sink: com.example.leaktest.MainActivity: void sendOrderedBroadcast(android.content.Intent,java.lang.String)

## 24.Timy

- destimy.android/file_selector
  - Source:  android.telephony.gsm.GsmCellLocation: int getLac()
  - Sink: java.io.Writer: void write(java.lang.String)
- destimy.androids/share_file
  - Source: java.io.File: java.io.File getAbsoluteFile()
  - Sink:  java.io.OutputStream: void write(byte[],int,int)
- destimy.android/save_as
  - Source: android.database.Cursor: java.lang.String getString(int)
  - Sink:  java.io.Writer: void write(char[])
- destimy.android/file_io
  - Source: android.telephony.TelephonyManager: java.lang.String getLine1Number()
  - Sink: android.util.Log: int w(java.lang.String,java.lang.String)
- dart_wormhole_william
  - Source: java.util.Locale: java.lang.String getCountry()
  - Sink: android.util.Log: int w(java.lang.String,java.lang.String)

## 25.ShockAlarm

- Channel: shock_alarm_permissions
  - Source: android.database.Cursor: java.lang.String getString(int)
  - Sink: android.os.Bundle: void putStringArray(java.lang.String,java.lang.String[])
- Channel: shock_alarm_protocol
  - Source: android.database.Cursor: java.lang.String getString(int)
  - Sink: android.os.Bundle: void putStringArrayList(java.lang.String,java.util.ArrayList)

## 26.Hacki

- Channel: dev.britannio.in_app_review

  - Source:  android.telephony.gsm.GsmCellLocation: int getLac()

  - Sink:  java.io.OutputStream: void write(byte[])

- synced_shared_preferences

  - Source:  java.io.File: java.io.File getAbsoluteFile()
  - Sink:  java.io.Writer: void write(char[])

## 27.Thingsboard

- Channel: org.talkingpanda/irtransmitte
  - Source: java.util.Locale: java.lang.String getCountry()
  - Sink:android.os.Bundle: void putString(java.lang.String,java.lang.String)

## 28.Lighthouse

- Channel: com.jeroen1602.lighthouse_pm/bluetooth
  - Source: java.io.File: java.io.File getAbsoluteFile()
  - Sink: android.os.Handler: boolean sendMessage(android.os.Message)
- Channel: com.jeroen1602.lighthouse_pm/shortcut
  - Source: java.io.File: java.io.File getCanonicalFile()
  - Sink: java.io.OutputStream: void write(byte[])
- Channel: com.jeroen1602.lighthouse_pm/IAP
  - Source: android.accounts.AccountManager: android.accounts.Account[] getAccounts()
  - Sink: java.io.OutputStream: void write(byte[],int,int)

## 29.Group-track

- Channel: geofence_plugin
  - Source: android.bluetooth.BluetoothAdapter: java.lang.String getAddress()
  - Sink: java.io.OutputStream: void write(byte[],int,int)

## 30.piggyvault

- Channel: app.channel.shared.data
  - Source: android.telephony.gsm.GsmCellLocation: int getCid()
  - Sink: java.io.Writer: void write(char[])
