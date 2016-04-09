package com.averagecoder.wargame.engine;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.utils.Utils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {

    private Client client;
    private NetworkController controller;

    public void init(NetworkController c){
        controller = c;
        client = c.client;
    }

    @Override
    public void connected(Connection c){
        controller.connected = true;
        System.out.println("[CLIENT] >> connected");
    }

    @Override
    public void disconnected(Connection c){
        controller.connected = false;
        System.out.println("[CLIENT] >> disconnected");
    }

    @Override
    public void received(Connection c, Object o){
        if(o instanceof Packets.PacketUserData){
            Packets.PacketUserData p = (Packets.PacketUserData)o;

            Utils.log("[SERVER] >> " + p.message);

            WargameCampaign.networkController.parseUserData(p);

        }else if(o instanceof Packets.PacketGameData){
            Packets.PacketGameData p = (Packets.PacketGameData)o;

            Utils.log("[SERVER] >> " + p.message);

            WargameCampaign.networkController.parseGameData(p);

        }else if(o instanceof Packets.PacketFriendTasks){
            Packets.PacketFriendTasks p = (Packets.PacketFriendTasks)o;

            Utils.log("[SERVER] >> " + p.message);

            WargameCampaign.networkController.parseFriendData(p);

        }
    }
}
