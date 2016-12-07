package net.hjord.shoppinglist.Models;

/**
 * Created by Hjord on 17/11/2016.
 */

public class Member {

    public String getUser() {
        return user;
    }

    public void setUser(String mUser) {
        this.user = mUser;
    }

    private String user;

    public Member() {
    }

    public Member(String user) {
        this.user = user;
    }

}
