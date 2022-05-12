package bgu.spl.net.srv;


import java.util.ArrayList;

public class Message {
    private final short operation;
    private ArrayList<String> arrayList;

    public Message(){
        operation = 0;
        arrayList = new ArrayList<>();
    }

    public Message(short operation, ArrayList<String> arrayList){
        this.operation=operation;
        this.arrayList=arrayList;
    }
    public ArrayList<String> getList(){
        return this.arrayList;
    }
    public short getOp(){
        return this.operation;
    }

    public String getUserName(){
        return this.arrayList.get(0);
    }
    public String getPassWord(){
        return this.arrayList.get(1);
    }

    public Integer getCourseNum(){
        String s = this.arrayList.get(0).replaceFirst("\u0000", "");
        s = s.replaceFirst(" ","");
        return Integer.parseInt(s);
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
