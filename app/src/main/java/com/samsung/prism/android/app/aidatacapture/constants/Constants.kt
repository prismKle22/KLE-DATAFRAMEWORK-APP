package com.samsung.prism.android.app.aidatacapture.constants

object Constants {

    //Always update these version number before rolling out
    //For major upload update this version
    const val APK_VERSION_NO=0
    //For minor updates update this version
    const val APK_SUB_VERSION_NO=3
    const val SHARED_PREF_ADMIN_EMAIL="admin@klesamsung"
    const val BASE_URL = "http://210.212.192.31/"
//    const val BASE_URL = "http://192.168.0.110/" //tenda
    const val ERROR="java.net.ConnectException: Failed to connect to /210.212.192.31:80"
    const val FILE_UPLOAD_URL = BASE_URL + "phps/fileUpload.php"
    const val LABELED_IMAGE_URL = BASE_URL + "imageLabelingHelper.php"
    const val AGREEMENT_UPLOAD_URL = BASE_URL + "phps/uploadAgreement.php"
    const val HUMAN_CENTRIC_AGREEMENT_UPLOAD_URL = BASE_URL + "uploadHumanCentricAgreement.php"
    const val AUDIO_UPLOAD_URL = BASE_URL +"uploadAudio.php"
    const val SAMPLE_FILE = "kleprivacypolicy.pdf"
    const val TAG = "MSG"
    const val IMAGE_DIRECTORY_NAME = ".KLE-SAMSUNG Data Capture Framework"
    const val SDCARD_PATH = "/sdcard/"
    const val SHARED_PREP_NAME ="AIDataLabSharedPref"
    const val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    const val TEXT_CENTRIC= "Text Centric"
    const val PEOPLE_CENTRIC="People Centric"
    const val NON_PEOPLE_CENTRIC="Non People Centric"
    const val TOTAL_IMAGE_VIDEO="Total Images & Videos"
    const val TOTAL_AUDIO="Total Audios"
    const val REQUEST_CODE_PERMISSIONS = 1
    const val REQUEST_CODE_READ_STORAGE = 2
    const val REGISTER_PDF_NAME="Register.pdf"
    const val CONSENT_PDF_NAME="Consent.pdf"
    const val KLETECH_PRIVACY_POLICY="kleprivacypolicy.pdf"
    const val SAMSUNG_PRIVACY_POLICY=""
    const val HUMAN_CENTRIC_AGREEMENT="humanCentricAgreement"
    const val DESCRIPTION="description"
    const val SIZE="size"
    const val SUB_CATEGORY="SubCat"
    const val USER_NAME="userName"
    const val DATA_TYPE="dataType"
    const val DEVICE_USED="deviceUsed"
    const val RESOLUTION="resolution"
    const val DSLR_MOBILE="dslrOrMobile"
    const val DATA_MAJOR_CATEGORY="dataMajorCategory"
    const val TYPE="type"
    const val CONSENT_OBTAINED="consentObtained"
    const val HUMAN_PRESENT="humanPresent"
    const val DEBUG = false // Set to true to enable logging
    const val MIME_TYPE_AUDIO = "audio/*"
    const val MIME_TYPE_TEXT = "text/*"
    const val MIME_TYPE_IMAGE = "image/*"
    const val MIME_TYPE_VIDEO = "video/*"
    const val MIME_TYPE_APP = "application/*"
    const val HIDDEN_PREFIX = "."
    const val AGREEMENT="agreement"
    const val MEDIA_TYPE_IMAGE = 1
    const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    const val SPEECH = "Speech"
    const val SPEAKER = "Speaker"
    const val NOISE = "Noise"
    const val PDF = "Pdf"
    const val TEXT = "Text"
    const val DOC = "Doc"
    const val CORPUSSMS ="Corpus Sms"
    const val TRANSLATION="Translation"
    const val REQUEST_CODE_PICK_IMAGE = 101
    enum class ViewTypeForAudio{
        NOISE,
        SPEECH,
        SPEAKER
    }
    enum class ViewTypeForSuperMetaText{
        CORPUSSMS,
        TEXT,
        TRANSLATION
    }
    enum class ViewTypeForText{
        PDF,
        TEXT,
        DOC
    }
    const val COLLECT_SOUND_DIRECTORY="/.record_sound_hidden_folder/"
    const val SOUND_FILE_NAME="Recording_"
}