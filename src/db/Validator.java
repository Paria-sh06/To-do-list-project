package db;

import exception.InvalidEntityException;

public interface Validator {
    void validate(Entity entity) throws InvalidEntityException;
}
