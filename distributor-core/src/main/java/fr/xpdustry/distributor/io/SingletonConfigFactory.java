package fr.xpdustry.distributor.io;

import org.aeonbits.owner.*;
import org.aeonbits.owner.loaders.*;

import java.util.*;


public final class SingletonConfigFactory implements Factory{
    private static final SingletonConfigFactory INSTANCE = new SingletonConfigFactory();

    public static SingletonConfigFactory getInstance(){
        return INSTANCE;
    }

    @Override public <T extends Config> T create(Class<? extends T> clazz, Map<?, ?>... imports){
        return ConfigFactory.create(clazz, imports);
    }

    @Override public String getProperty(String key){
        return ConfigFactory.getProperty(key);
    }

    @Override public String setProperty(String key, String value){
        return ConfigFactory.setProperty(key, value);
    }

    @Override public String clearProperty(String key){
        return ConfigFactory.clearProperty(key);
    }

    @Override public Properties getProperties(){
        return ConfigFactory.getProperties();
    }

    @Override public void setProperties(Properties properties){
        ConfigFactory.setProperties(properties);
    }

    @Override public void registerLoader(Loader loader){
        ConfigFactory.registerLoader(loader);
    }

    @Override public void setTypeConverter(Class<?> type, Class<? extends Converter<?>> converter){
        ConfigFactory.setTypeConverter(type, converter);
    }

    @Override public void removeTypeConverter(Class<?> type){
        ConfigFactory.removeTypeConverter(type);
    }
}
