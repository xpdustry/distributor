package fr.xpdustry.distributor.plugin.internal;

import com.fasterxml.jackson.annotation.*;

import java.util.*;
import javax.xml.bind.annotation.*;


@JsonPropertyOrder({"root-path", "roles"})
@XmlRootElement(name = "distributor")
public class Settings{
    @XmlElement(name = "root-path")
    public String rootPath = "./distributor/";
    @XmlElementWrapper(name = "roles")
    public List<Role> roles = Role.getDefault();
}
