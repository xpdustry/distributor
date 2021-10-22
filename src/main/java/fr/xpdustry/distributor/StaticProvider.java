package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;

import fr.xpdustry.distributor.plugin.settings.Settings;

import com.ctc.wstx.api.*;
import com.ctc.wstx.stax.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.*;
import com.fasterxml.jackson.module.jaxb.*;

import javax.xml.stream.*;
import java.io.*;


public class StaticProvider{
    /** Based on https://github.com/FasterXML/jackson-dataformat-xml */
    public static XmlMapper createXML(){
        // Woodstox XMLInputFactory impl
        XMLInputFactory inputFactory = new WstxInputFactory();
        inputFactory.setProperty(WstxInputProperties.P_MAX_ATTRIBUTE_SIZE, 32000);

        // Woodstox XMLOutputFactory impl
        XMLOutputFactory outputFactory = new WstxOutputFactory();
        outputFactory.setProperty(WstxOutputProperties.P_OUTPUT_CDATA_AS_TEXT, true);

        XmlFactory factory = XmlFactory.builder()
            .inputFactory(inputFactory)
            .outputFactory(outputFactory)
            .build();

        XmlMapper xml = new XmlMapper(factory);
        xml.registerModule(new JaxbAnnotationModule());
        xml.enable(SerializationFeature.INDENT_OUTPUT);

        return xml;
    }

    public static Settings createSettings(ObjectMapper mapper){
        // Look for distributor.xml in the server directory
        Fi config = new Fi(Core.files.external("./distributor.xml").absolutePath());
        Settings settings = new Settings();

        try{
            if(config.exists()) settings = mapper.readValue(config.read(), Settings.class);
            else mapper.writeValue(config.file(), settings);
        }catch(IOException e){
            Log.err("An error occurred while saving the setting file, fallback to default settings.", e);
        }

        return settings;
    }
}
