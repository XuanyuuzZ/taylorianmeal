package taylorianmeal;

/**
 * A simple singleton that keeps the ID of the currently logged-in user in memory.
 */
public final class LoginSession {

    private static final LoginSession INSTANCE = new LoginSession();
    private int userId = -1;        // -1 means not logged in

    private LoginSession() { }

    /** Global access point */
    public static LoginSession get() { return INSTANCE; }

    public void setUserId(int id)   { this.userId = id; }
    public int  getUserId()         { return userId; }
}
