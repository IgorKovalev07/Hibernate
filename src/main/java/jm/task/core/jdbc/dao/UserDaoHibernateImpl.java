package jm.task.core.jdbc.dao;


import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import jakarta.persistence.*;
import org.hibernate.Transaction;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        String sql = "CREATE TABLE users (Id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(20)," + "lastName VARCHAR(20), age TINYINT)";
        try (Session session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица создана");
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS users";
        try (Session session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createNativeQuery(sql).executeUpdate();
            session.getTransaction().commit();
            System.out.println("Таблица удалена");
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = new User(name, lastName, age);
            session.save(user);
            transaction.commit();
            System.out.println("Пользователь сохранен");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Ошибка при сохранении пользователя");
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            session.delete(user);
            session.getTransaction().commit();
            System.out.println("User c " + id + " удален");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Ошибка удаления " + e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        String hql = "from User";
        try (Session session = Util.getSessionFactory().openSession()) {
            return session.createQuery(hql).list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void cleanUsersTable() {
        String hql = "delete from User";
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery(hql).executeUpdate();
            transaction.commit();
            System.out.println("Таблица очищена");
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new HibernateException("Ошибка при удалении " + e);
        }
    }
}



