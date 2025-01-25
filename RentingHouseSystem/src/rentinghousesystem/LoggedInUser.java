package rentinghousesystem;

public class LoggedInUser {
    private static int ownerId;

    public static void setOwnerId(int id) {
        ownerId = id;
    }

    public static int getOwnerId() {
        return ownerId;
    }

    static int getUserId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
