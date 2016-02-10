package ru.dz.dhtpatch;

import lombok.extern.java.Log;

@Log
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "uTorrent DHT patch.");
        try{
            new Patcher().start();
        } catch (Exception e){
            log.severe("Finished with error");
        }

    }
}
