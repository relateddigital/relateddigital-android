package com.relateddigital.relateddigital_android.constants

import com.relateddigital.relateddigital_android.model.VisilabsParameter

class Constants {
    companion object {
        const val LOG_TAG = "Constants"
        const val SDK_MIN_API_VERSION: Int = 21
        const val RELATED_DIGITAL_MODEL_KEY = "related_digital_model_key"

        const val COOKIE_ID_KEY = "cookie_id_key"
        const val EURO_CONSENT_TIME_KEY = "ConsentTime"
        const val LAST_EVENT_TIME_KEY = "last_event_time"
        const val PVIV_KEY = "pviv"
        const val TVC_KEY = "tvc"
        const val COOKIE_ID_REQUEST_KEY = "OM.cookieID"
        const val DOMAIN_REQUEST_KEY = "OM.domain"
        const val EXVISITOR_ID_REQUEST_KEY = "OM.exVisitorID"
        const val CHANNEL_REQUEST_KEY = "OM.vchannel"
        const val TOKEN_ID_REQUEST_KEY = "OM.sys.TokenID"
        const val APP_ID_REQUEST_KEY = "OM.sys.AppID"
        const val ORGANIZATION_ID_REQUEST_KEY = "OM.oid"
        const val SITE_ID_REQUEST_KEY = "OM.siteID"
        const val DATE_REQUEST_KEY = "dat"
        const val URI_REQUEST_KEY = "OM.uri"
        const val MAPPL_REQUEST_KEY = "OM.mappl"
        const val SDK_VERSION_REQUEST_KEY = "sdk_version"
        const val SDK_TYPE_REQUEST_KEY = "sdk_type"
        const val APP_VERSION_REQUEST_KEY = "OM.appVersion"
        const val API_VERSION_REQUEST_KEY = "OM.apiver"
        const val NRV_REQUEST_KEY = "OM.nrv"
        const val PVIV_REQUEST_KEY = "OM.pviv"
        const val TVC_REQUEST_KEY = "OM.tvc"
        const val LVT_REQUEST_KEY = "OM.lvt"
        const val ADVERTISER_ID_REQUEST_KEY = "OM.m_adid"
        const val VISITOR_DATA_REQUEST_KEY = "OM.viscap"
        const val VISIT_DATA_REQUEST_KEY = "OM.vcap"
        const val USER_AGENT_REQUEST_KEY = "User-Agent"
        const val OM_3_REQUEST_KEY = "OM.3rd"
        const val LOAD_BALANCE_PREFIX = "NSC"
        const val OM_3_KEY = "OM.3rd"
        const val PROMOTION_CODE_REQUEST_KEY = "OM.promoaction"
        const val ACTION_ID_REQUEST_KEY = "OM.actionid"
        const val PROMOTION_CODE_EMAIL_REQUEST_KEY = "OM.promoemail"
        const val PROMOTION_CODE_TITLE_REQUEST_KEY = "OM.promotitle"
        const val PROMOTION_CODE_SLICE_TEXT_REQUEST_KEY = "OM.promoslice"
        const val APP_TRACKER_REQUEST_KEY = "OM.apptracker"
        const val LOGIN_REQUEST_KEY = "Login"
        const val B_LOGIN_KEY_REQUEST_KEY = "OM.b_login"
        const val SIGN_UP_REQUEST_KEY = "SignUp"
        const val B_SIGN_UP_KEY_REQUEST_KEY = "OM.b_sgnp"
        const val APP_BANNER_PARAMETER_KEY = "OM.OSB"

        const val REQUEST_TYPE_KEY = "type"
        const val REQUEST_AUTH_KEY = "auth"
        const val REQUEST_SUBS_ACTION_ID_KEY = "actionid"
        const val REQUEST_SUBS_EMAIL_KEY = "OM.subsemail"
        const val REQUEST_ACTION_ID_KEY = "action_id"
        const val REQUEST_ACTION_TYPE_KEY = "action_type"
        const val REQUEST_ACTION_TYPE_VAL = "MailSubscriptionForm~SpinToWin~ScratchToWin~ProductStatNotifier~drawer~GiftRain~FindToWin~AppBanner~ShakeToWin~GiftBox~ChooseFavorite~SlotMachine~MobileCustomActions~MobileAppRating~ClawMachine"


        var VISILABS_PARAMETERS: List<VisilabsParameter>? = null
        const val TARGET_PREF_VOSS_KEY = "OM.OSS"
        const val TARGET_PREF_VCNAME_KEY = "OM.cname"
        const val TARGET_PREF_VCMEDIUM_KEY = "OM.cmedium"
        const val TARGET_PREF_VCSOURCE_KEY = "OM.csource"
        const val TARGET_PREF_VSEG1_KEY = "OM.vseg1"
        const val TARGET_PREF_VSEG2_KEY = "OM.vseg2"
        const val TARGET_PREF_VSEG3_KEY = "OM.vseg3"
        const val TARGET_PREF_VSEG4_KEY = "OM.vseg4"
        const val TARGET_PREF_VSEG5_KEY = "OM.vseg5"
        const val TARGET_PREF_BD_KEY = "OM.bd"
        const val TARGET_PREF_GN_KEY = "OM.gn"
        const val TARGET_PREF_LOC_KEY = "OM.loc"
        const val TARGET_PREF_VPV_KEY = "OM.pv"
        const val TARGET_PREF_LPVS_KEY = "OM.pv"
        const val TARGET_PREF_LPP_KEY = "OM.pp"
        const val TARGET_PREF_VQ_KEY = "OM.q"
        const val TARGET_PREF_VRDOMAIN_KEY = "OM.rDomain"
        const val TARGET_PREF_PPR_KEY = "OM.ppr"

        const val TARGET_PREF_VOSS_STORE_KEY = "OM.voss"
        const val TARGET_PREF_VCNAME_STORE_KEY = "OM.vcname"
        const val TARGET_PREF_VCMEDIUM_STORE_KEY = "OM.vcmedium"
        const val TARGET_PREF_VCSOURCE_STORE_KEY = "OM.vcsource"
        const val TARGET_PREF_VSEG1_STORE_KEY = "OM.vseg1"
        const val TARGET_PREF_VSEG2_STORE_KEY = "OM.vseg2"
        const val TARGET_PREF_VSEG3_STORE_KEY = "OM.vseg3"
        const val TARGET_PREF_VSEG4_STORE_KEY = "OM.vseg4"
        const val TARGET_PREF_VSEG5_STORE_KEY = "OM.vseg5"
        const val TARGET_PREF_BD_STORE_KEY = "OM.bd"
        const val TARGET_PREF_GN_STORE_KEY = "OM.gn"
        const val TARGET_PREF_LOC_STORE_KEY = "OM.loc"
        const val TARGET_PREF_VPV_STORE_KEY = "OM.vpv"
        const val TARGET_PREF_LPVS_STORE_KEY = "OM.lpvs"
        const val TARGET_PREF_LPP_STORE_KEY = "OM.lpp"
        const val TARGET_PREF_VQ_STORE_KEY = "OM.vq"
        const val TARGET_PREF_VRDOMAIN_STORE_KEY = "OM.vrDomain"

        const val SHOWN_STORIES_PREF = "VisilabsShownStories"
        const val SHOWN_STORIES_PREF_KEY = "shownStories"

        const val PAGE_NAME_REQUEST_VAL = "/OM_evt.gif"

        const val INTENT_ID_KEY = "INTENT_ID_KEY"

        const val REMOTE_CONFIG_BLOCK_PREF = "visilabs_block_pref"
        const val REMOTE_CONFIG_BLOCK_PREF_KEY = "visilabs_block_pref_key"

        const val STORY_CIRCLE = "50%"
        const val STORY_ROUNDED_RECTANGLE = "10%"
        const val STORY_RECTANGLE = ""
        const val STORY_LOOKING_BANNERS = "story_looking_banners"
        const val STORY_SKIN_BASED = "skin_based"
        const val STORY_ITEM_POSITION = "story_item_position"
        const val STORY_POSITION = "position"
        const val ACTION_DATA = "action"
        const val ACTION_ID = "action_id"
        const val STORY_PHOTO_KEY = "photo"
        const val STORY_VIDEO_KEY = "video"
        const val STORY_ACTION_TYPE_VAL = "Story"
        const val NPS_ACTION_TYPE_VAL = "nps_with_numbers"

        const val LOCATION_PERMISSION_REQUEST_KEY = "OM.locpermit"
        const val LOC_PERMISSION_ALWAYS_REQUEST_VAL = "always"
        const val LOC_PERMISSION_APP_OPEN_REQUEST_VAL = "appopen"
        const val LOC_PERMISSION_NONE_REQUEST_VAL = "none"

        const val ZONE_ID_KEY = "OM.zid"
        const val BODY_KEY = "OM.body"
        const val FILTER_KEY = "OM.w.f"

        const val FavoriteAttributeAction = "FavoriteAttributeAction"

        const val NOTIFICATION_PERMISSION_REQUEST_KEY = "OM.pushnotifystatus"

        const val GEOFENCE_LATITUDE_KEY = "OM.latitude"
        const val GEOFENCE_LONGITUDE_KEY = "OM.longitude"
        const val GEOFENCE_ACT_KEY = "act"
        const val GEOFENCE_ACT_ID_KEY = "actid"
        const val GEOFENCE_GEO_ID_KEY = "OM.locationid"
        const val GEOFENCE_ACT_VALUE = "getlist"
        const val GEOFENCE_PROCESS_VALUE = "process"

        const val NOTIFICATION_TRANSPARENT_SMALL_ICON = "small_icon"
        const val NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE = "small_icon_dark_mode"
        const val NOTIFICATION_LARGE_ICON = "large_icon"
        const val NOTIFICATION_LARGE_ICON_DARK_MODE = "large_icon_dark_mode"
        const val NOTIFICATION_USE_LARGE_ICON = "use_large_icon"
        const val INTENT_NAME = "intent_name"
        const val NOTIFICATION_COLOR = "notification_color"
        const val CHANNEL_NAME = "channel_name"
        const val BADGE = "badge"
        const val ACTIVE = 1
        const val PASSIVE = 0
        const val LAST_SUBS_DATE_KEY = "last_subscription_date"
        const val LAST_SUBS_KEY = "last_subscription"

        const val EMAIL_PERMIT_KEY = "emailPermit"
        const val GSM_PERMIT_KEY = "gsmPermit"
        const val TWITTER_KEY = "twitter"
        const val FACEBOOK_KEY = "facebook"
        const val EMAIL_KEY = "email"
        const val SET_ANONYMOUS_KEY = "setAnonymous"
        const val LOCATION_KEY = "location"
        const val RELATED_DIGITAL_USER_KEY = "keyID"
        const val MSISDN_KEY = "msisdn"
        const val CONSENT_SOURCE_KEY = "ConsentSource"
        const val CONSENT_SOURCE_VALUE = "HS_MOBIL"
        const val RECIPIENT_TYPE_KEY = "RecipientType"
        const val RECIPIENT_TYPE_BIREYSEL = "BIREYSEL"
        const val RECIPIENT_TYPE_TACIR = "TACIR"
        const val CONSENT_TIME_KEY = "ConsentTime"

        const val PAYLOAD_SP_KEY = "payload_sp"
        const val PAYLOAD_SP_ARRAY_KEY = "messages"

        const val NOTIFICATION_ID = "NotificationId"
        const val EVENT_CAROUSAL_ITEM_CLICKED_KEY = "CarouselItemClicked"
        const val CAROUSAL_SET_UP_KEY = "CAROUSAL_SET_UP_KEY"
        const val EVENT_LEFT_ARROW_CLICKED = 1
        const val EVENT_RIGHT_ARROW_CLICKED = 2
        const val EVENT_LEFT_ITEM_CLICKED = 3
        const val EVENT_RIGHT_ITEM_CLICKED = 4
        const val CAROUSAL_IMAGE_BEGENNING = "CarouselImage"
        const val CAROUSAL_SMALL_ICON_FILE_NAME = "smallIconCarousel"
        const val CAROUSAL_LARGE_ICON_FILE_NAME = "largeIconCarousel"
        const val CAROUSAL_PLACEHOLDER_ICON_FILE_NAME = "placeHolderIconCarousel"
        const val CAROUSAL_ITEM_CLICKED_KEY = "CarouselItemClickedKey"
        const val CAROUSEL_ITEM_CLICKED_URL = "CarouselItemClickedUrl"
        const val NOTIFICATION_CHANNEL_ID_KEY = "not_channel_id_key"
        const val NOTIFICATION_CHANNEL_NAME_KEY = "not_channel_name_key"
        const val NOTIFICATION_CHANNEL_DESCRIPTION_KEY = "not_channel_description_key"
        const val NOTIFICATION_CHANNEL_SOUND_KEY = "not_channel_sound_key"
        const val NOTIFICATION_PRIORITY_KEY = "not_priority_key"

        const val UTM_SOURCE = "utm_source"
        const val UTM_MEDIUM = "utm_medium"
        const val UTM_CAMPAIGN = "utm_campaign"
        const val UTM_CONTENT = "utm_content"
        const val UTM_TERM = "utm_term"

        const val BUTTON_LINK = "link"
        const val BUTTON_REDIRECT = "redirect"
        const val BUTTON_COPY_REDIRECT = "copy_redirect"

        const val PAYLOAD_SP_ID_KEY = "payload_sp_with_id"
        const val PAYLOAD_SP_ARRAY_ID_KEY = "messages_with_id"
        const val NOTIFICATION_LOGIN_ID_KEY = "notification_login_id_key"

        const val BANNER_CAROUSEL_ACTION_TYPE_VAL = "AppBanner"

        const val SUBSCRIPTION_ENDPOINT = "https://pushs.euromsg.com/"
        const val RETENTION_ENDPOINT = "https://pushr.euromsg.com/"

        const val LOGGER_ENDPOINT = "https://lgr.visilabs.net/"
        const val REALTIME_ENDPOINT = "https://rt.visilabs.net/"
        const val GRAYLOG_ENDPOINT = "https://rd-gateway-log.relateddigital.com/"
        const val REMOTE_CONFIG_ENDPOINT = "https://mbls.visilabs.net/"

        var ACTION_ENDPOINT = "https://s.visilabs.net/"

        const val PUSH_REGISTER_EVENT = "com.relateddigital.relateddigital_android.push.intent.REGISTER"
        const val PUSH_RECEIVE_EVENT = "com.relateddigital.relateddigital_android.push.intent.RECEIVE"
    }
}