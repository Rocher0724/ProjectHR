package haru.com.hr.domain;

/**
 * Created by myPC on 2017-03-27.
 */

public class EmailSet {
    String email;
    String password;

    public EmailSet(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
