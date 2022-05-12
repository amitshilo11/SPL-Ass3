package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                Integer.parseInt(args[0]),   //port
                CourseProtocol::new,         //protocol factory
                CourseEncoderDecoder::new    //message encoder decoder factory
        ).serve();
    }
}
