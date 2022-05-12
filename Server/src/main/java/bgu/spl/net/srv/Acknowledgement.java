package bgu.spl.net.srv;

import java.nio.ByteBuffer;

public class Acknowledgement extends Message{
    private final short operation;
    private final short messageOp;

    public Acknowledgement(short op){
        this.operation = 12;
        this.messageOp = op;
    }

    public byte[] encodeAck(){
        byte[] op=shortToBytes(this.operation);
        byte[] messageOp=shortToBytes(this.messageOp);
        int i = 0;
        String theList="";
        for (String item: this.getList()){
            theList=theList+item+"\0";
        }
        byte[] list=theList.getBytes();
        if(theList.equals(""))
            i = 1;
        byte[] allByteArray=new byte[4+i+list.length];
        ByteBuffer buff=ByteBuffer.wrap(allByteArray);
        buff.put(op);
        buff.put(messageOp);
        buff.put(list);
        if(theList.equals(""))
            buff.put((byte) '\0');
        return buff.array();
    }
}