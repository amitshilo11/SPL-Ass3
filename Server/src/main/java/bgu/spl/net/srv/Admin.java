package bgu.spl.net.srv;

public class Admin {
    private String userName;
    private String password;

    public Admin(String userName, String password){
        this.userName=userName;
        this.password=password;
    }
    public String getPassword(){
        return this.password;
    }
}
