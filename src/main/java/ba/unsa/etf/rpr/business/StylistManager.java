package ba.unsa.etf.rpr.business;

import ba.unsa.etf.rpr.dao.Dao;
import ba.unsa.etf.rpr.dao.DaoFactory;
import ba.unsa.etf.rpr.domain.Stylist;
import ba.unsa.etf.rpr.exceptions.HairsalonException;

import java.util.List;
/**
 * This is a Java class called "StylistManager" that provides a set of methods for managing Stylist objects.
 * The class uses the DaoFactory to access the Stylist DAO (Data Access Object) and perform CRUD (Create, Read, Update, Delete) operations on Stylist objects.
 * It also throws a HairsalonException in case of an error.
 * @author Nejra Adilovic
 */
public class StylistManager {
    /**
     * Adds Stylist object to table STYLIST.
     * @param s Stylist
     * @return added Stylist
     * @throws HairsalonException in case of problems with db
     */
    public Stylist add(Stylist s) throws HairsalonException {
        if (s.getId() != 0) {
            throw new HairsalonException("Can't add Stylist with ID. ID is autogenerated");
        }
        return DaoFactory.stylistDao().add(s);
    }
    /**
     * Deletes stylist, from db table STYLIST, with a given id.
     * @param id int
     * @throws HairsalonException thrown in case of problem with db
     */
    public void delete(int id) throws HairsalonException {
        DaoFactory.stylistDao().delete(id);
    }
    /**
     * Updates stylist in db table STYLIST.
     * @param s Stylist
     * @return Stylist that is updated
     * @throws HairsalonException thrown in case of problem with db
     */
    public void update(Stylist s) throws HairsalonException {
        DaoFactory.stylistDao().update(s);
    }
    /**
     * Fetches all Stylist objects from table STYLIST and stores it in a list.
     * @return List of all stylists
     * @throws HairsalonException thrown in case of problem with db
     */
    public List<Stylist> getAll() throws HairsalonException {
        return DaoFactory.stylistDao().getAll();
    }
    /**
     * Fetches Stylist object form table STYLIST defined by given id.
     * @param stylistId int
     * @return Stylist object defined by given id
     * @throws HairsalonException
     */
    public Stylist getById(int stylistId) throws HairsalonException{
        return DaoFactory.stylistDao().getById(stylistId);
    }
}
