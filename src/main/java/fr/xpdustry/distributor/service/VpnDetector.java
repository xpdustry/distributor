package fr.xpdustry.distributor.service;


public interface VpnDetector{
    boolean isRateLimited();

    boolean isVpn(String ip);
}
