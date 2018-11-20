package de.thm.ap.leistungen.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Module;
import de.thm.ap.leistungen.model.Module;

public class ModuleDAO {
    private static String FILE_NAME = "Modules.obj";
    private Context ctx;
    private List<Module> modules = new ArrayList<>();
    private int nextId = 1;

    public ModuleDAO(Context ctx) {
        this.ctx = ctx;
        initModules();
    }
    public List<Module> findAll() { return modules; }

    public List<Module> findById(int id) {
        List<Module> listModule = new ArrayList<>();
        for(Module module : modules){
            if(module.getId().equals(id)){
                listModule.add(module);
            }
        }
        return listModule;
    }


    public void persist(Module Module) {
        Module.setId(nextId++);
        modules.add(Module);
    }

    public void persistAll(Module... modules){
        for(Module module : modules) persist(module);
        saveModules();
    }

    public void delete(Module module) {
        if(findById(module.getId()).size() > 0) modules.removeAll(findById(module.getId()));
        saveModules();
    }

    public void deleteAll(Module... modules) {
        for(Module module : modules) delete(module);
    }

    @SuppressWarnings("unchecked")
    public void initModules() {
        File f = ctx.getFileStreamPath(FILE_NAME);
        if (f.exists()) {
            try (FileInputStream in = ctx.openFileInput(FILE_NAME)) {
                Object obj = obj = new ObjectInputStream(in).readObject();
                modules = (List<Module>) obj;
// init next id
                modules.stream()
                        .mapToInt(Module::getId)
                        .max()
                        .ifPresent(id -> nextId = id + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            modules = new ArrayList<>();
        }
    }
    public void saveModules() {
        try (FileOutputStream out = ctx.openFileOutput(FILE_NAME, Context.
                MODE_PRIVATE)) {
            new ObjectOutputStream(out).writeObject(modules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
