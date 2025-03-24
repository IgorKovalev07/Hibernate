package jm.task.core.jdbc.dao;


import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import jakarta.persistence.*;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    public UserDaoHibernateImpl() {
    }


    @Override
    public void createUsersTable() {
        String sql = "CREATE TABLE users (Id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(20)," +
                "lastName VARCHAR(20), age TINYINT)";
        Session session = null;
        try {
            session = Util.getSessionFactory().openSession();
            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица создана");
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
//        Util.getSessionFactory().close();
    }

    @Override
    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS users";
        Session session = null;
        try {
            session = Util.getSessionFactory().openSession();
            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица удалена");
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
//            Util.getSessionFactory().close();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Session session = null;
        try {
            session = Util.getSessionFactory().openSession();
            session.beginTransaction();
            User user = new User(name, lastName, age);
            session.save(user);
            session.getTransaction().commit();
            System.out.println("Пользователь сохранен");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
                throw new HibernateException("Ошибка при сохранении");
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
//        Util.getSessionFactory().close();
    }


    @Override
    public void removeUserById(long id) {
        try (Session session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                session.getTransaction().commit();
                System.out.println("User c " + id + " удален");
            } else {
                System.out.println("Пользователь с "+id+" не найден");
                session.getTransaction().rollback();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            // попробовал сделать rollback через try-with-resources, но как мне показалось
            //лучше это делать через блок try catch finally, без открытия новой сэссии
            try (Session session = Util.getSessionFactory().openSession()) {
                if (session != null && session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//       Util.getSessionFactory().close();
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "from User";
        try (Session session = Util.getSessionFactory().openSession()) {
            return session.createQuery(sql).list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void cleanUsersTable() {
        String sql = "DELETE FROM users";
        Session session = null;
        try {
            session = Util.getSessionFactory().openSession();
            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица очищена");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
                throw new HibernateException("Ошибка при удалении");
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
//        Util.getSessionFactory().close();
    }

}


