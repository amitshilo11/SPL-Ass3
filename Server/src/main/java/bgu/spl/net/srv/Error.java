package bgu.spl.net.srv;

import java.nio.ByteBuffer;

public class Error extends Message{
    private final short operation;
    private final short messageOp;

    public Error(short op){
        this.operation = 13;
        this.messageOp = op;
    }
    public byte[] encodeErr(){
        byte[] op=shortToBytes(this.operation);
        byte[] messageOp=shortToBytes(this.messageOp);
        byte[] allByteArray=new byte[4];
        ByteBuffer buff=ByteBuffer.wrap(allByteArray);
        buff.put(op);
        buff.put(messageOp);
        return buff.array();
    }
}
