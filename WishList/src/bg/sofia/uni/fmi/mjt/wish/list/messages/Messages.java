package bg.sofia.uni.fmi.mjt.wish.list.messages;

public enum Messages {

    SUCCESSFULLY_REGISTERED("[ Username %s successfully registered ]"),
    DISCONNECT_OK("[ Disconnected from server ]"), DISCONNECT_FAIL("[ Could not disconnect ]"),
    MISSING_USERNAME_AND_PASSWORD("[ Missing username and password ]"),
    USERNAME_EXISTS("[ Username %s is already taken, select another one ]"),
    IVALID_USERNAME("[ Username %s is invalid, select a valid one ]"),
    SUCCESSFULLY_LOGGED_IN("[ User %s successfully logged in ]"),
    SUCCESSFULLY_LOGGED_OUT("[ Successfully logged out ]"),
    ALREADY_LOGGED_IN("[ You have already logged in ]"),
    INVALID_COMBINATION("[ Invalid username/password combination ]"),
    GIFT_SUBMITTED_SUCCESSFULLY("[ Gift %s for student %s submitted successfully ]"),
    GIFT_ALREADY_SUBMITTED("[The same gift for student %s was already submitted ]"),
    STUDENT_NOT_REGISTERED("[ Student with username %s is not registered ]"),
    NOT_LOGGED_IN("[ You are not logged in ]"),
    NO_STUDENTS_IN_WISH_LIST("[ There are no students present in the wish list ]"),
    CANT_VIEW_OWN_GIFTS("[ You can't view your own gifts ]"),
    NO_COMMAND("[ No command entered ]"), UNKNOWN_COMMAND("[ Unknown command ]"),
    SERVER_CLOSED("[ The server shutdown unexpectedly ]"),
    STOP_ALL_CLIENTS("themostrandomstringtoKILLTHEMALL12345678900987654321"),
    BUFFER_MESSAGE(""),
    RESPONSE_TEXT("");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}