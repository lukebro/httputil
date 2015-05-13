package com.github.lukebro.tests;

import com.github.lukebro.httputil.*;

public class RequestPacketTest {

    public static void main(String[] args)
    {
        HttpRequest request;

        request = HttpRequest
                    .post("search.php")
                    .to("http://example.com")
                    .from("lukebrodowski@gmail.com")
                    .data("Some sample data.");


        byte[] requestBytes = request.toBytes();
        String requestString = request.toString();

        System.out.print(requestString);

    }
}
