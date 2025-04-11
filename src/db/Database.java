package db;

import exception.EntityNotFoundException;
import exception.InvalidEntityException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database {
    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static int nextId = 1;
    private static HashMap<Integer, Validator> validators = new HashMap<>();

    private Database() {
    }

    public static void add(Entity e) throws InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if (validator != null) {
            validator.validate(e);
        }

        Entity copy = e.copy();
        copy.setId(nextId++);
        if (copy instanceof Trackable) {
            Date now = new Date();
            Trackable trackable = (Trackable) copy;
            trackable.setCreationDate(now);
            trackable.setLastModificationDate(now);
        }
        entities.add(copy);
        e.setId(copy.getId());
    }



    public static Entity get(int id) {
        for (Entity entity : entities) {
            if (entity.getId() == id) {
                return entity.copy();
            }
        }
        throw new EntityNotFoundException(id);
    }

    public static void delete(int id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId() == id) {
                entities.remove(i);
                return;
            }
        }
        throw new EntityNotFoundException(id);
    }

    public static void update(Entity e) throws InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if (validator != null) {
            validator.validate(e);
        }

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId() == e.getId()) {
                Entity copy = e.copy();

                if (copy instanceof Trackable) {
                    Date now = new Date();
                    ((Trackable) copy).setLastModificationDate(now);
                }

                entities.set(i, copy);
                return;
            }
        }
        throw new EntityNotFoundException(e.getId());
    }


    public static void registerValidator(int entityCode, Validator validator) {
        if (validators.containsKey(entityCode)) {
            throw new IllegalArgumentException("Validator for entityCode " + entityCode + " already exists.");
        }
        validators.put(entityCode, validator);
    }

    public static ArrayList<Entity> getAll(int entityCode) {
        ArrayList<Entity> result = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity.getEntityCode() == entityCode) {
                result.add(entity);
            }
        }

        return result;
    }


}
