import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.dao.DaoFactory;
import ba.unsa.etf.rpr.dao.UserDao;
import ba.unsa.etf.rpr.domain.Stylist;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.HairsalonException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserManagerTest {
    private UserManager userManager = new UserManager();
    @Test
    void testSearchById(){
        try {
            assertEquals("Edwin",userManager.getById(1).getFirst_name());
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testSearchByFirstName(){
        try {
            assertEquals("ahromic1", userManager.getByFirstName("Amina").getUsername());
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testSearchByLastName(){
        try {
            assertEquals("062877807", userManager.getByLastName("Selimovic").getPhone());
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testSearchByUsername(){
        try {
            assertTrue(userManager.getByUsername("nadilovic2").isAdmin(), "true");
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testUserIsAdmin(){
        try {
            assertEquals("nadilovic2@etf.unsa.ba", userManager.getByAdmin(true).getEmail());
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testAddUser(){
        User user = new User();
        user.setFirst_name("Sara");
        user.setLast_name("Biogradlic");
        user.setPhone("062001333");
        user.setEmail("sbiogradlic1@gmail.com");
        user.setAdmin(false);
        user.setUsername("sbiogradlic1");
        user.setPassword("stajeovoovo");
        boolean added=false;
        try {
            userManager.add(user);
            List<User> lista = userManager.getAll();
            for(User u: lista){
                if(u.getUsername().equals("sbiogradlic1")){
                    userManager.delete(u.getId());
                    added=true;
                    break;
                }
            }
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
        assertTrue(added);
    }
    @Test
    void testDeleteUser(){
        User user = new User();
        user.setFirst_name("Sara");
        user.setLast_name("Biogradlic");
        user.setPhone("062001333");
        user.setEmail("sbiogradlic1@gmail.com");
        user.setAdmin(false);
        user.setUsername("sbiogradlic1");
        user.setPassword("stajeovoovo");
        boolean deleted=false;
        try {
            userManager.add(user);
            List<User> lista = userManager.getAll();
            for(User u: lista){
                if(u.getUsername().equals("sbiogradlic1")){
                    userManager.delete(u.getId());
                    deleted=true;
                    break;
                }
            }
        } catch (HairsalonException e) {
            throw new RuntimeException(e);
        }
        assertTrue(deleted);
    }
    @Test
    void testGetAll(){
        assertDoesNotThrow(() -> { userManager.getAll(); });
    }

}