package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

/**
 * Clasa ce valideaza un utilizator
 */
public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    /**
     * throws validException when:
     *                  -firstName is null or contains symbols that are not letters and " ","-"
     *                  -lastName is null or contains symbols that are not letters and " ","-"
     */
    public void validate(Utilizator entity) throws ValidationException {
        //TODO: implement method validate
        if (entity.getFirstName() == null)
            throw new ValidationException("Prenume null!\n");
        if (!entity.getFirstName().matches("^[a-zA-Z -]+$"))//daca contine atceva decat litere, spati
            throw new ValidationException("Prenumele contine caractere invalide!\n");
        if (entity.getLastName() == null)
            throw new ValidationException("Nume null!\n");
        if (!entity.getLastName().matches("^[a-zA-Z -]+$"))//daca contine atceva decat litere, spati/
            throw new ValidationException("Numele  contine caractere invalide!\n");

    }
}