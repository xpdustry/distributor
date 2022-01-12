package fr.xpdustry.distributor.util;

import mindustry.gen.*;


public class MockPlayer extends Player{
    public MockPlayer(){
    }

    @Override public void sendMessage(String text){
        lastText = text;
    }
}
