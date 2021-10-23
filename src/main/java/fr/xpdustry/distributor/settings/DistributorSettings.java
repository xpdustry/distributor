package fr.xpdustry.distributor.settings;

import com.fasterxml.jackson.annotation.*;

import javax.xml.bind.annotation.*;
import java.util.*;


@JsonPropertyOrder({"root-path", "roles"})
@XmlRootElement(name = "distributor")
public class DistributorSettings{
    @XmlElement(name = "root-path")
    private String rootPath = "./distributor/";

    @XmlElementWrapper(name = "roles")
    private List<Role> roles = Role.getDefault();

    public String getRootPath(){
        return rootPath;
    }

    public void setRootPath(String rootPath){
        this.rootPath = Objects.requireNonNull(rootPath, "The rootPath is null.");
    }

    public List<Role> getRoles(){
        return roles;
    }

    public void setRoles(List<Role> roles){
        this.roles = Objects.requireNonNull(roles, "The roles are null.");
    }
}
