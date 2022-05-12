package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MessagingEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private short operation=0;
    private ArrayList<String> arrayList=new ArrayList<>();
    private byte[] byteArr = new byte[1 << 10];
    private int len=0;

    public Message decodeNextByte(byte nextByte) {
        if (operation == 0) {
            if (len == 2) {
                operation = byteToShort(byteArr);
            }
            if (operation == 4 | operation == 11) {
                Message toReturn = new Message(operation, arrayList);
                clear();
                return toReturn;
            }
            pushByte(nextByte);
            return null;
        }
        if (operation == 1 | operation == 2 | operation == 3 | operation == 8) {
            if (nextByte == ' ') {
                popValue();
                if (operation == 8) {
                    Message toReturn = new Message(operation, arrayList);
                    clear();
                    return toReturn;
                } else if (arrayList.size() == 2) {
                    Message toReturn = new Message(operation, arrayList);
                    clear();
                    return toReturn;
                }
                pushByte(nextByte);
                return null;
            }
            pushByte(nextByte);
            return null;
        }
         else { //operation=5,6,7,9,10
            if (len == 1) {
                pushByte(nextByte);
                short o = byteToShort(byteArr);
                arrayList.add(Short.toString(o));
                Message toReturn = new Message(operation, arrayList);
                clear();
                return toReturn;
            }
            pushByte(nextByte);
            return null;
        }
    }

    private void popValue(){
            String result = new String(byteArr, 0, len, StandardCharsets.UTF_8);
            len = 0;
            arrayList.add(result);
    }
    private void pushByte(byte nextByte){
        if(len>=byteArr.length){
            byteArr = Arrays.copyOf(byteArr, len*2);
        }
        byteArr[len] = nextByte;
        len = len + 1;
    }
    public short byteToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        len = 0;
        return result;
    }

    public byte[] encode(Message message) {
        if (message==null) {
            clear();
            return null;
        }
        if (message instanceof Acknowledgement)
            return ((Acknowledgement) message).encodeAck();
        else
            return ((Error) message).encodeErr();
    }
    public void clear(){
        operation = 0;
        arrayList = new ArrayList<>();
        byteArr = new byte[1 << 10];
        len = 0;
    }
}
