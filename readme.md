# NFC HCE NDEF Example 3

This app is NOT using the sample AID of the original project but the AID for NDEF messages

source: https://github.com/justinribeiro/android-hostcardemulation-sample

Org Sample AID: F0394148148100

NDEF AID: D2760000850101

Flow see: https://stackoverflow.com/questions/29122848/ndef-message-with-hce-android

https://stackoverflow.com/questions/67329010/nfc-host-card-emulation-hce-on-android-not-working-on-particular-devices

https://stackoverflow.com/questions/23155719/host-card-emulation-on-android-4-4-kitkat-and-above-with-nexus-5

https://github.com/sfomuseum/android-nfc-clock/blob/master/app/src/main/java/org/sfomuseum/nfcclock/CardService.kt





```plaintext
<aid-group android:description="@string/aiddescription"
        android:category="other">
        <aid-filter android:name="F0394148148100" />
        <aid-filter android:name="A0000000031010" />
    </aid-group>
```

ORG apduservice.xml
```plaintext
    <aid-group android:description="@string/aiddescription"
        android:category="other">
        <!-- Visa Proximity Payment System Environment - PPSE (2PAY.SYS.DDF01) -->
        <aid-filter android:name="325041592E5359532E4444463031" />
        <!-- VISA Debit/Credit (Classic)  -->
        <aid-filter android:name="A0000000031010" />
        <!-- VISA Credit -->
        <aid-filter android:name="A000000003101001" />
        <!-- VISA Debit -->
        <aid-filter android:name="A000000003101002" />
        <!-- VISA Electron (Debit) -->
        <aid-filter android:name="A0000000032010" />
        <!-- V PAY -->
        <aid-filter android:name="A0000000032020" />
        <!-- VISA Interlink -->
        <aid-filter android:name="A0000000033010" />
        <!-- MasterCard PayPass -->
        <aid-filter android:name="A00000000401" />
        <!-- MasterCard Credit -->
        <aid-filter android:name="A0000000041010" />
        <!-- American Express -->
        <aid-filter android:name="A000000025" />
        <!-- BRADESCO -->
        <aid-filter android:name="F0000000030001" />
        <!-- Sample for the demo application -->
        <aid-filter android:name="F0394148148100" />
        <!-- NFC type 4 NDEF -->
        <aid-filter android:name="D2760000850101" />
    </aid-group>
```




# Android Host Card Emulation of a NFC Forum Type-4 tag

This example emulates a NFC Forum Type-4 tag with an a single NDEF record (RTD_TEXT). The project uses aid-filter F0394148148100 (which is an example that is defined in the Android documentation) for the APDU SELECT command.

## Whoa there what?

The NFC Forum Type 4 Tag Operation Specification 3.0 outlines how to interact with tags. Using Host Card Emulation in Android, we can do something pretty nifty:

1. We can write an application on the card reader side that sends the proper APDU SELECT and associated commands and interact with a "card".
2. That "card" in this case is emulated in our Android application.

## Where can I get the tag operational spec?

There are various copies on the Internet, but the place to go is to sign the specification license and download direct from the [NFC Forum document library](http://members.nfc-forum.org/specs/spec_license/document_form/).

## How do I interact with the Android application through a card reader?

You have to follow the commands in your client application. You can also compile and run the sample in the apdu_exchange_tester directory that is based on the APDU examples from libNFC (for detailed instructions, see the readme in that folder).

## Where can I learn more about Host Card Emulation on Android?

You'll need API 19 (aka Android 4.4) to use HCE. See [Host-based Card Emulation @ developers.android.com](https://developer.android.com/guide/topics/connectivity/nfc/hce.html) for all the details.

## What if I want to interact with or read a Type-4 tag?

This is a different sort of thing and you'll want to look into [ISO-DEP](http://developer.android.com/reference/android/nfc/tech/IsoDep.html).


source: https://stackoverflow.com/a/29127714/8166854

Emulating a tag that is detected as NDEF tag using Android HCE is not as simple as sending an NDEF message in response to a SELECT APDU. You would need to implement the NFC Forum Type 4 Tag Operation specification. You can get that specification from the NFC Forum website.

Basically you would need to register a HCE service for the AID D2760000850101 that implements a couple of APDU commands that the reader-side uses to access a Type 4 tag:

SELECT NDEF tag application

00 A4 04 00 07 D2760000850101 [00]
SELECT capability container

00 A4 00 0C 02 E103
SELECT NDEF data file

00 A4 00 0C 02 xxyy
Where xxyy is the file ID of the NDEF data file as specified in the capability container.

READ BINARY (for reading data from capability container or NDEF data file, whichever is currently selected)

00 B0 xx yy zz
Where xx yy is the offset to read at and zz is the number of bytes to read.

Important note: Be aware that such an NFC Forum Type 4 tag emulated by an Android device cannot be used to automatically trigger an app on a second Android device (at least not reliably?). Putting two Android devices together will usually result in them establishing a peer-to-peer link (even if Beam is turned off!). Only a foreground app on the second Android device could use the NFC Reader mode API to bypass Android Beam and reliably detect the emulated tag.


source: https://stackoverflow.com/a/51212229/8166854

You can define virtually any APDU command for HCE. Only the initial SELECT (by AID) command is required. After that, you can create your own command set (or try to follow ISO/IEC 7816-4 commands) as long as you obey the rules of ISO/IEC 7816 for command/response APDU structure, and stick to valid CLA, INS, and status word values.

Since you only want to transfer an ID, you could send this ID directly in response to the SELECT command:

private static final String ID = "1234567890"

@Override
public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
byte[] responseApdu = new byte[] { (byte)0x6F, (byte)0x00 };

    if ((commandApdu != null) && (commandApdu.length >= 4)) {
        if ((commandApdu[0] == (byte)0x00) && (commandApdu[1] == (byte)0xA4) && (commandApdu[2] == (byte)0x04) && (commandApdu[3] == (byte)0x00)) {
            Log.i("HCEDEMO", "Application selected");

            byte[] id = ID.getBytes(Charset.forName("UTF-8"));
            responseApdu = new byte[id.length + 2];
            System.arraycopy(id, 0, responseApdu, 0, id.length);
            responseApdu[id.length] = (byte)0x90;
            responseApdu[id.length + 1] = (byte)0x00;
        }
    }
    return responseApdu;
}


sample app (old): https://github.com/grundid/host-card-emulation-sample


https://developer.android.com/guide/topics/connectivity/nfc/hce



