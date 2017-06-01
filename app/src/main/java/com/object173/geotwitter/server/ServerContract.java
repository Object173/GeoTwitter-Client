package com.object173.geotwitter.server;

public final class ServerContract {

    public static final String HOST = "http://37.192.46.135:8443/";

    private static final String AUTH_CONTROLLER = "/auth";
    static final String REQUEST_SIGN_IN = AUTH_CONTROLLER + "/sign_in/";
    static final String REQUEST_REGISTER = AUTH_CONTROLLER + "/register/";

    private static final String PROFILE_CONTROLLER = "/profile";
    static final String REQUEST_SET_FCM_TOKEN = PROFILE_CONTROLLER + "/set_fcm_token/";
    static final String REQUEST_SET_USERNAME = PROFILE_CONTROLLER + "/set_username/";
    static final String REQUEST_SET_STATUS = PROFILE_CONTROLLER + "/set_status/";
    static final String REQUEST_SET_PASSWORD = PROFILE_CONTROLLER + "/set_password/";
    static final String REQUEST_SET_AVATAR = PROFILE_CONTROLLER + "/set_avatar/";
    static final String REQUEST_REMOVE_AVATAR = PROFILE_CONTROLLER + "/remove_avatar/";

    private static final String CONTACTS_CONTROLLER = "/contacts";
    static final String REQUEST_GET_ALL_CONTACTS = CONTACTS_CONTROLLER + "/get_all/";
    static final String REQUEST_SEND_INVITE = CONTACTS_CONTROLLER + "/send_invite/";
    static final String REQUEST_REMOVE_INVITE = CONTACTS_CONTROLLER + "/remove_invite/";

    private static final String DIALOGS_CONTROLLER = "/dialogs";
    static final String REQUEST_GET_DIALOG = DIALOGS_CONTROLLER + "/get";
    static final String REQUEST_GET_ALL_DIALOGS = DIALOGS_CONTROLLER + "/get_all";
    static final String REQUEST_GET_MESSAGES = DIALOGS_CONTROLLER + "/get_messages";
    static final String REQUEST_GET_LAST_MESSAGES = DIALOGS_CONTROLLER + "/get_last_messages";
    static final String REQUEST_SEND_MESSAGE = DIALOGS_CONTROLLER + "/send_message";

    private static final String PLACE_CONTROLLER = "/place";
    static final String REQUEST_ADD_PLACE = PLACE_CONTROLLER + "/add";
    static final String REQUEST_GET_LIST_PLACE = PLACE_CONTROLLER + "/get_list";
    static final String REQUEST_GET_LIST_ALL_PLACE = PLACE_CONTROLLER + "/get_list_all";
    static final String REQUEST_GET_LAST_PLACE = PLACE_CONTROLLER + "/get_last";
    static final String REQUEST_GET_LAST_ALL_PLACE = PLACE_CONTROLLER + "/get_last_all";
    static final String REQUEST_GET_ALL_PLACE = PLACE_CONTROLLER + "/get_all";

    public static final String ATTR_AVATAR_NAME = "avatar";
    public static final String ATTR_IMAGE_NAME = "image";
    public static final String ATTR_IMAGE_LIST_NAME = "images";

    public static final String MEDIA_TYPE_FILE = "multipart/form-data";

    public static final String NOTIFICATION_DATA_TYPE = "type";
    public static final String NOTIFICATION_DATA_OBJECT = "object";

    public static final String NOTIFICATION_TYPE_INVITE= "invite";
    public static final String NOTIFICATION_TYPE_CHANGE_USER= "change_user";
    public static final String NOTIFICATION_TYPE_CHANGE_AVATAR= "change_avatar";
    public static final String NOTIFICATION_TYPE_NEW_DIALOG = "new_dialog";
    public static final String NOTIFICATION_TYPE_NEW_MESSAGE= "new_message";

    public static final int PLACE_TITLE_MIN_LENGTH = 10;
    public static final int PLACE_BODY_MIN_LENGTH = 10;

    public static String getAbsoluteUrl(final String url) {
        return HOST + url;
    }
}
