package bgu.spl.net.impl.BGRSServer;
import java.io.Serializable;

public class Message implements Serializable {

    private short opcode;
    private short secondOpcode;
    private String firstString;
    private String secondString;

    Message(short opcode, String firstString, String secondString){
        this.opcode = opcode;
        this.firstString = firstString;
        this.secondString = secondString;
    }
    Message(short opcode, String firstString){
        this.opcode = opcode;
        this.firstString = firstString;
    }

    Message(short opcode, short secondOpcode){
        this.opcode = opcode;
        this.secondOpcode = secondOpcode;
    }
    Message(short opcode, short secondOpcode, String firstString){
        this.opcode = opcode;
        this.secondOpcode = secondOpcode;
        this.firstString = firstString;
    }

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public String getFirstString() {
        return firstString;
    }

    public String getSecondString() {
        return secondString;
    }

    public short getSecondOpcode() {
        return secondOpcode;
    }
}
