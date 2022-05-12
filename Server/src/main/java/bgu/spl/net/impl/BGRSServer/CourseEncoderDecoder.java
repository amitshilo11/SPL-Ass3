package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.DefinitionsAndVariablesSet;
import bgu.spl.net.api.MessageEncoderDecoder;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CourseEncoderDecoder implements MessageEncoderDecoder<Message>, DefinitionsAndVariablesSet {

    private byte[] buff = new byte[1 << 10]; //start with 1k
    private int position = 0;
    private short opcode;
    private MessageType type = null;
    private Queue<Integer> contentIndexes = new LinkedList<>();

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {


        if (position < 2) {
            pushByte(nextByte);
            return null;
        }
        if (position == 2) {
            setOpcodeType();
            if (opcode == 0) {
                resetAfterGet();
                return null;
            }
        }

        if (type.name().equals("CourseNum")) {
            if (position == 4)
                return popMessage();
        }
        else {
            if (nextByte == 0) {
                contentIndexes.add(position);
                if (contentIndexes.size() == type.numOfContent)
                    return popMessage();
            }
        }
        pushByte(nextByte);
        return null; //not a commend yet
        }

        @Override
        public byte[] encode(Message message) {
        byte[] opcodeArr = shortToBytes(message.getOpcode());

            switch (message.getOpcode()) {
                case ERR: {
                    byte[] opcodeArr3 = shortToBytes(message.getSecondOpcode());
                    byte[] arrResult = new byte[4];
                    arrResult[0] = opcodeArr[0];
                    arrResult[1] = opcodeArr[1];
                    arrResult[2] = opcodeArr3[0];
                    arrResult[3] = opcodeArr3[1];

                    return arrResult;
                }
                case ACK: {
                    byte[] opcodeArr4 = shortToBytes(message.getSecondOpcode());
                    byte[] opcodeArr2 = message.getFirstString().getBytes(StandardCharsets.UTF_8);
                    byte[] result2 = new byte[4 + opcodeArr2.length + 1];
                    result2[0] = opcodeArr[0];
                    result2[1] = opcodeArr[1];
                    result2[2] = opcodeArr4[0];
                    result2[3] = opcodeArr4[1];

                    for (int i = 4; i < result2.length - 1; i++)
                        result2[i] = opcodeArr2[i - 4];

                    result2[result2.length - 1] = '\0';
                    return result2;
                }
            }
            return null;
        }

        private void pushByte(byte nextByte) {
            if (position >= buff.length) {
                buff = Arrays.copyOf(buff, position * 2);
            }

            buff[position++] = nextByte;
        }

        private Message popMessage () {
            Message msg = null;

            switch (type) {
                case UsernameAndPass:
                    String username = new String(buff, 2, -2 + contentIndexes.peek(), StandardCharsets.UTF_8);
                    String password = new String(buff, contentIndexes.peek() + 1, -contentIndexes.poll() + contentIndexes.peek(), StandardCharsets.UTF_8);
                    msg = new Message(opcode, username, password);
                    break;


                case CourseNum:
                    short courseNumber = Array.getShort(buff,2);
                    msg = new Message(opcode, courseNumber);
                    break;

                case UsernameString:
                    String studentUsername = new String(buff, 2, contentIndexes.poll() - 1, StandardCharsets.UTF_8);
                    msg = new Message(opcode, studentUsername);
                    break;
            }
            resetAfterGet();
            return msg;
        }

        private void resetAfterGet() {
            position = 0;
            contentIndexes.clear();
            type = null;
            opcode = 0;
        }

        private void setOpcodeType() {
            byte[] shortArray = new byte[2];
            shortArray[0] = buff[0];
            shortArray[1] = buff[1];

            short code = bytesToShort(shortArray);
            this.opcode = code;
            if ((code < 1) || (10 < code)) {
                this.opcode = UNDEFINE;
            }

            switch (opcode) {
                case UNDEFINE:
                    type = null;
                    break;
                case ADMINREG:
                case STUDENTREG:
                case LOGIN:
                    type = MessageType.UsernameAndPass;
                    break;
                case LOGOUT:
                case MYCOURSES:
                    type = MessageType.NoAdditionalFields;
                    break;
                case COURSEREG:
                case KDAMCHECK:
                case COURSESTAT:
                case ISREGISTERED:
                case UNREGISTER:
                    type = MessageType.CourseNum;
                    break;
                case STUDENTSTAT:
                    type = MessageType.UsernameString;
                    break;
            }
        }
}