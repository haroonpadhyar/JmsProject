package com.mine.jms.orm;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PersonRepository extends HibernateDaoSupport {

    @Autowired
    public PersonRepository(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    @Transactional
    public void saveRecord(){
        //// Just for Transaction Testing purpose.
//        if(true){
//            throw new HibernateException("Test");
//        }
        getSessionFactory().getCurrentSession().createSQLQuery("insert into person (id, name) values ('1', 'AAAA')").executeUpdate();
    }


}
