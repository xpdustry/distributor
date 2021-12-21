package fr.xpdustry.distributor.internal;

import arc.struct.*;

import mindustry.mod.Mods.*;

import org.checkerframework.checker.nullness.qual.*;

import static java.util.Objects.requireNonNull;


/**
 * This {@code ClassLoader} uses same technique as the ModClassLoader of V7.
 */
public class SharedClassLoader extends ClassLoader{
    /** Shared mod list */
    private final @NonNull Seq<LoadedMod> children;
    private final ThreadLocal<Boolean> inChild = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public SharedClassLoader(@NonNull ClassLoader parent, @NonNull Seq<LoadedMod> children){
        super(parent);
        this.children = requireNonNull(children, "children can't be null.");
    }

    @Override
    protected Class<?> findClass(@NonNull String name) throws ClassNotFoundException{
        // A child may try to delegate class loading to its parent, which is *this class loader* - do not let that happen
        if(inChild.get()){
            inChild.set(false);
            throw new ClassNotFoundException(name);
        }

        ClassNotFoundException last = null;
        int size = children.size;

        // If it doesn't exist in the main class loader, try all the children
        for(int i = 0; i < size; i++){
            // We share the mod list so avoid NullPointersExceptions with js mods
            if(children.get(i).loader == null) continue;

            try{
                try{
                    inChild.set(true);
                    return children.get(i).loader.loadClass(name);
                }finally{
                    inChild.set(false);
                }
            }catch(ClassNotFoundException e){
                last = e;
            }
        }

        throw (last == null ? new ClassNotFoundException(name) : last);
    }
}
