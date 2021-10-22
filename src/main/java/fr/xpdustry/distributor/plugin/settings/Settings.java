package fr.xpdustry.distributor.plugin.settings;

import com.fasterxml.jackson.annotation.*;

import javax.xml.bind.annotation.*;
import java.util.*;


@JsonPropertyOrder({"root-path", "roles"})
@XmlRootElement(name = "distributor")
public class Settings{
    @XmlElement(name = "root-path")
    public String rootPath = "./distributor/";
    @XmlElementWrapper(name = "roles")
    public List<Role> roles = Role.getDefault();
}
