package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {

        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                CourseProtocol::new,         //protocol factory
                CourseEncoderDecoder::new   //message encoder decoder factory
        ).serve();
    }
}