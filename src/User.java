import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String profileInfo;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.profileInfo = "";
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void updateProfile(String profileInfo) {
        this.profileInfo = profileInfo;
        System.out.println("Profile updated successfully.");
    }

    public void updatePassword(String oldPassword, String newPassword) {
        if (checkPassword(oldPassword)) {
            this.password = newPassword;
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Incorrect old password.");
        }
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Profile Info: " + profileInfo;
    }
}
