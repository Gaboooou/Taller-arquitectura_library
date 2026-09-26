package cl.ucn.disc.arqsist.library.dao.Interface;


import java.util.List;

public interface CrudDao<T> {

    List<T> findAll();
    T findById(int id);
    void create(T entity);
    void update(T entity);
    void delete(T entity);

}
