package com.robin.service.exc4;


import io.rebloom.client.Client;

public class BloomTest {

    public static void main(String[] args) {

        Client client = new Client("127.0.0.1", 6379);

        client.delete("codehole");
        for (int i = 0; i < 100000; i++) {
            client.add("codehole", "user" + i);
            boolean ret = client.exists("codehole", "user" + (i + 1));
            if (ret) {
                System.out.println(i);
                break;
            }
        }
        client.close();
    }
}
