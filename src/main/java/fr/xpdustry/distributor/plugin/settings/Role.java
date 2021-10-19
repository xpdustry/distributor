package fr.xpdustry.distributor.plugin.settings;

import com.fasterxml.jackson.annotation.*;

import java.util.*;


public class Role{
    public String name;
    public boolean admin;
    public long access;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Role(@JsonProperty("name") String name, @JsonProperty("admin") boolean admin, @JsonProperty("access") long access){
        this.name = name;
        this.admin = admin;
        this.access = access;
    }

    public static List<Role> getDefault(){
        return Arrays.asList(
            new Role("Admin", true, 2000),
            new Role("Player", false, 1000)
        );
    }
}
