package ltd.finelink.tool.disk.enums;

public enum BizErrorCode {

    /**
     * B represent for Business
     * error code start with "B" means errors happen with business
     * B00XY represent app business errors
     * B01XY represent config business errors
     * B02XY represent user business errors
     * B03XY represent group business errors
     * B04XY represent message errors
     * B05XY represent faq errors
     * B06XY represent feedback errors
     * B07XY represent role errors
     *
     * common rules:
     * Y = 1 already existed
     * Y = 2 param error
     * Y = 3 exceed
     * Y = 4 not exist
     * Y = 5 forbidden
     */

    /**
     * B01XY config errors:
     * B010Y message config errors:
     */

    UNKNOWN_ERROR("B0000", "internal business error"),


    /**
     * B001Y app info errors:
     * B0011 app exists
     * B0014 app not exists
     */
    APP_EXISTS("B0011", "app or platform not exist"), 

    APP_NOT_EXISTS("B0014", "app or platform not exist"),
    
    AUDIT_CHAT_TIMEOUT("B0025","audit chat time out"),

    MAX_MESSAGE_EXCEED("B0103", "Max Chat Message Exceed"),
    STRANGER_FORBIDDEN("B0105", "not allow talk with stranger"),
    
    

    /**
     * use errors:
     * B020Y user info errors
     * B021Y user auth errors
     * B022Y user contact errors
     * B023Y user blacklist errors
     * B024Y user push token errors
     */
    USER_EXIST("B0201", "user already exists"),
    USER_PARAM_ERROR("B0202", "user info parameter error"),
    USER_NOT_EXIST("B0204", "user not exists"),

    USER_AUTH_PARAM_ERROR("B0212", "user auth param error"),

    USER_AUTH_NOT_EXIST("B0214", "user auth not exist"),

    USER_CONTACT_EXIST("B0221", "user contact already exist"),

    USER_CONTACT_NOT_EXIST("B0224", "user contact not exist"),

    BLACKLIST_EXIST("B0231", "blacklist already exists"),
    BLACKLIST_NOT_EXIST("B0234", "blacklist not exists"),
    SENDER_IN_BLACKLIST("B0235", "sender in receiver's Blacklist"),


    USER_PUSH_TOKEN_EXIST("B0241", "user push token exists"),
    USER_PUSH_TOKEN_PARAM_ERROR("B0242", "user push token parameters error"),
    USER_PUSH_TOKEN_NOT_EXIST("B0244", "user push token not exists"),
    /**
     * group errors:
     */
    GROUP_EXIST("B0301", "group already exists"),


    GROUP_PARAM_ERROR("B0302", "group params error"),

    GROUP_NOT_EXIST("B0304", "group not exists"),

    GROUP_IS_FORBIDDEN("B0305", "group is forbidden"),

    GROUP_CONFIG_SILENT("B0306", "group is set to be silent"),
    GROUP_MEMBER_EXIST("B0311", "user already in group"),

    GROUP_MEMBER_PARAM_ERROR("B0312", "group member parameters error"),

    GROUP_MEMBER_NOT_EXIST("B0314", "user is not in group"),
    GROUP_MEMBER_IS_FORBIDDEN("B0315", "user in group has been banned "),


    /**
     * message errors:
     * B030Y represent single chat errors
     * B031Y represent group chat errors
     * B032Y represent custom chat errors
     * B033Y represent channel chat errors
     */
    MESSAGE_NOT_FOUND("B0404", "single chat message not found"),


    /**
     * B050Y represent faq category errors:
     * B051Y represent faq detail errors:
     * <p>
     * B0501 faq category already exists
     * B0504 faq category not exists
     */
    FAQ_CATEGORY_EXIST("B0501", "faq category already exists"),
    FAQ_CATEGORY_NOT_EXIST("B0504", "faq category not exists"),

    FAQ_DETAIL_EXIST("B0511", "faq detail already exists"),
    FAQ_DETAIL_NOT_EXIST("B0514", "faq detail not exists"),
    FAQ_DETAIL_IS_FORBIDDEN("B0515", "faq detail is forbidden"),

    /**
     * B060Y represent faq feedback question errors:
     * B061Y represent faq feedback reply errors:
     * <p>
     * B0601 feedback question already exists
     * B0604 feedback question not exists
     *
     * B0611 feedback reply already exists
     * B0614 feedback reply not exists
     *
     * B0621 feedback question type already exists
     * B0624 feedback question type not exists
     */
    FEEDBACK_QUESTION_EXIST("B0601", "feedback question already exists"),
    FEEDBACK_QUESTION_PARAM_ERROR("B0602", "feedback question params error"),
    FEEDBACK_QUESTION_NOT_EXIST("B0604", "feedback question not exists"),

    FEEDBACK_REPLY_EXIST("B0611", "feedback reply already exists"),
    FEEDBACK_REPLY_NOT_EXIST("B0614", "feedback reply not exists"),

    FEEDBACK_QUESTION_TYPE_EXIST("B0621", "feedback question type already exists"),

    FEEDBACK_QUESTION_TYPE_NOT_EXIST("B0624", "feedback question type not exists"),


    /**
     * B07XY represent role errors
     * B0701 role exists
     * B0704 role not exists
     */
    ROLE_EXIST("B0701", "role exists"),
    ROLE_NOT_EXIST("B0704", "role not exists"),

    /**
     * B08XY represent privilege errors
     * B0801 privilege exists
     * B0804 privilege not exists
     */
    PRIVILEGE_EXIST("B0801", "privilege exists"),
    PRIVILEGE_NOT_EXIST("B0804", "privilege not exists"),


    /**
     * B09XY represent moderation ticket errors
     * B0901 moderation ticket exists
     * B0904 moderation ticket not exists
     */
    MODERATION_TICKET_EXISTS("B0901", "moderation ticket exists"),
    MODERATION_TICKET_TYPE_ERROR("B0902", "moderation ticket type error"),
    MODERATION_TICKET_NOT_EXIST("B0904", "moderation ticket not exists"),
    NOT_THE_SAME_MODERATOR("B0905", "not the same moderator"),
    ;





    private String code;

    private String message;

    BizErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public int getIntCode() {
        return Integer.parseInt(code.substring(1));
    }

    public String getMessage() {
        return message;
    }

}
