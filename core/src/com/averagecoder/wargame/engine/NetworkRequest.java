package com.averagecoder.wargame.engine;

import com.averagecoder.wargame.WargameCampaign;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Client;

public class NetworkRequest implements Disposable {

    Packets.Packet packet;
    Client client;

    public NetworkRequest(Packets.Packet p){
        client = WargameCampaign.networkController.client;
        packet = p;

        addToQueue();
    }

    private synchronized void addToQueue(){
        WargameCampaign.networkController.queue.add(this);
    }

    public boolean parse(){

        try{
            client.sendTCP(packet);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        dispose();

        return true;
    }

    public synchronized void dispose(){
        packet = null;
    }
}
