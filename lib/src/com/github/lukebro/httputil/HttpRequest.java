package com.github.lukebro.httputil;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    /**
     *
     * A simple Java API extension for creating HTTP request packets manually.
     *
     */

    /**
     * Default HTTP Protocol.
     */
    protected String protocol = "HTTP/1.1";

    /**
     * Default User-Agent used in packets.
     */
    protected String userAgent = System.getProperty("java.version");

    /**
     * Default Accept-Charset for requests.
     */
    protected String acceptCharset = "ISO-8859-1,UTF-8;q=0.7,*;q=0.7";

    /**
     * Supported methods for HTTP/1.1.
     */
    private enum Method
    {
        OPTIONS,
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        TRACE,
        CONNECT
    }

    /**
     * Path of request.
     */
    private String path;

    /**
     * Method of request.
     */
    private Method method;

    /**
     * Stream of data for POST/PUT request.
     */
    private ByteArrayOutputStream data = new ByteArrayOutputStream();

    /**
     * Stream of data for header during build time of packet.
     */
    private ByteArrayOutputStream header = new ByteArrayOutputStream();

    /**
     * A key,value relationship between Field: Value.
     */
    private HashMap<String, String> fields = new HashMap<String, String>();

    /**
     * Return carriage for HTTP packet.
     */
    private static final String RETURN = "\r\n";


    /**
     * Create a new HttpRequest instance.
     *
     * @param method
     * @param path
     */
    private HttpRequest(Method method, String path)
    {
        this.method = method;
        this.path = path;

        this.fields.put("User-Agent", userAgent);
        this.fields.put("Accept-Charset", acceptCharset);
    }

    /**
     * Create a new OPTIONS request.
     *
     * @param path
     *
     * @return HttpRequest
     */
    public static HttpRequest options(String path)
    {
        return new HttpRequest(Method.OPTIONS, path);
    }

    /**
     * Create a new GET request.
     *
     * @param path
     *
     * @return HttpRequest
     */
    public static HttpRequest get(String path)
    {
        return new HttpRequest(Method.GET, path);
    }

    /**
     * Create a new GET request to "/".
     *
     * @return HttpRequest
     */
    public static HttpRequest get()
    {
        return new HttpRequest(Method.GET, "/");
    }

    /**
     * Create a new HEAD request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest head(String path)
    {
        return new HttpRequest(Method.HEAD, path);
    }

    /**
     * Create a new POST request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest post(String path)
    {
        return new HttpRequest(Method.POST, path);
    }


    /**
     * Create a new PUT request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest put(String path)
    {
        return new HttpRequest(Method.PUT, path);
    }

    /**
     * Create a new DELETE request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest delete(String path)
    {
        return new HttpRequest(Method.DELETE, path);
    }

    /**
     * Create a new TRACE request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest trace(String path)
    {
        return new HttpRequest(Method.TRACE, path);
    }

    /**
     * Create a new CONNECT request.
     *
     * @param path String
     *
     * @return HttpRequest
     */
    public static HttpRequest connect(String path)
    {
        return new HttpRequest(Method.CONNECT, path);
    }

    /**
     * Set the Host field.
     *
     * @param host String
     *
     * @return HttpRequest
     */
    public HttpRequest to(String host)
    {
        this.fields.put("Host", host);
        return this;
    }

    /**
     * Set the From field.
     *
     * @param email String
     *
     * @return HttpRequest
     */
    public HttpRequest from(String email)
    {
        this.fields.put("From", email);
        return this;
    }

    /**
     * Set the data of request using a byte array.
     *
     * @param data byte[]
     *
     * @return HttpRequest
     */
    public HttpRequest data(byte[] data)
    {
        this.addToData(data);
        return this;
    }

    /**
     * Set the data of a request using a String.
     *
     * @param data String
     *
     * @return HttpRequest
     */
    public HttpRequest data(String data)
    {
        return this.data(data.getBytes());
    }

    /**
     * Set the User-Agent of the request.
     *
     * @param userAgent String
     *
     * @return HttpRequest;
     */
    public HttpRequest agent(String userAgent)
    {
        this.fields.put("User-Agent", userAgent);
        return this;
    }

    /**
     * Set the Accept-Charset field of request.
     *
     * @param charset String
     *
     * @return HttpRequest
     */
    public HttpRequest charset(String charset)
    {
        this.fields.put("Accept-Charset", charset);
        return this;
    }

    /**
     * Get the HttpRequest in a byte array.
     *
     * @return byte[]
     */
    public byte[] toBytes()
    {
        return this.builtPacket();
    }

    /**
     * Get the HttpRequest in a String.
     *
     * @return String
     */
    public String toString()
    {
        return new String(this.builtPacket());
    }

    /**
     * Build the request packet.
     *
     * @return byte[]
     */
    private byte[] builtPacket()
    {
        this.buildHeader();

        ByteArrayOutputStream packet = new ByteArrayOutputStream();

        try
        {
            packet.write(this.header.toByteArray());
            packet.write(this.data.toByteArray());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return packet.toByteArray();
    }

    /**
     * Build the header of request.
     * Resets header output stream then creates
     * the request line and adds all fields.
     */
    private void buildHeader()
    {
        this.header.reset();

        this.buildRequest();
        this.buildFields();
    }

    /**
     * Build the first line of a request packet.
     * Adds blindly to the header output stream.
     */
    private void buildRequest()
    {
        String request = this.method + " " + this.path + " " + this.protocol + RETURN;
        this.addToHeader(request.getBytes());
    }

    /**
     * Build the fields inside the HashMap of fields.
     * Adds blindly to the header output stream.
     */
    private void buildFields()
    {

        for (Map.Entry<String, String> field : this.fields.entrySet())
        {
            this.addToHeader(field.getKey() + ": " + field.getValue() + RETURN);
        }

        this.addToHeader(RETURN);
    }


    /**
     * Writes data to the data byte array output stream.
     *
     * @param data byte[]
     */
    private void addToData(byte[] data)
    {
        try
        {
            this.data.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Writes data to the header byte array output stream.
     *
     * @param data byte[]
     */
    private void addToHeader(byte[] data)
    {
        try
        {
            this.header.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Writes data to the header byte array output stream.
     *
     * @param data String
     */
    private void addToHeader(String data)
    {
        this.addToHeader(data.getBytes());
    }



}
