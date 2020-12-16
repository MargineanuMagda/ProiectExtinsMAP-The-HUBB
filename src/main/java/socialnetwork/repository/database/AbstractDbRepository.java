package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDbRepository<ID, E extends Entity<ID>> implements PagingRepository<ID, E> {
    protected String url;
    protected String username;
    protected String password;
    protected Validator<E> validator;

    public AbstractDbRepository(String url, String username, String password, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");


        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findOneQuery(id));
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    return createEntity(resultSet);
                }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String findOneQuery(ID id);



    @Override
    public Iterable<E> findAll() {
        Set<E> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from "+getTableName());
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    E entity=createEntity(resultSet);
                    entities.add(entity);
                }
                return entities;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    protected abstract E createEntity(ResultSet resultSet) throws SQLException;

    protected abstract String getTableName();


    //protected abstract Iterable<E> findAllQuery(Set<E> entities, Connection connection);

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new RepositoryException("Repository exception: id must be not null!\n");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            return entity;
        } else {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = addQuery(entity, connection)) {
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * sql query for adding an entity
     * @param entity- entity we want to add
     * @param connection-connection established
     * @return a statement
     * @throws SQLException if sql query fails
     */
    protected abstract PreparedStatement addQuery(E entity, Connection connection) throws SQLException;

    @Override
    public E delete(ID id) {
        if (id == null)
            throw new RepositoryException("Repository exception: id must be not null!\n");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = delQuery(id, connection)) {
            E entity = findOne(id);
            statement.execute();
            return entity;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *sql query for deleting an entity
     * @param id-id of the user we want to delete
     * @param connection-connection established
     * @return a statement
     * @throws SQLException if sql query fails
     */
    protected abstract PreparedStatement delQuery(ID id, Connection connection) throws SQLException;

    @Override
    public E update(E entity) {
        if (entity == null)
            throw new RepositoryException("Repository exception: entity must be not null!");
        validator.validate(entity);


        if (findOne(entity.getId()) != null) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = updateQuery(entity, connection)) {
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        return entity;
    }

    /**
     * sql query for updating an entity
     * @param entity we want to update
     * @param connection  connection established
     * @return a statement
     * @throws SQLException if sql query does not work
     */
    protected abstract PreparedStatement updateQuery(E entity, Connection connection) throws SQLException;

    @Override
    public  int size(){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) from "+getTableName())
        ) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        Paginator<E> paginator = new Paginator<>(pageable,this.findAll());
        return paginator.paginate();
    }
}
