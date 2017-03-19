package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.server.model.Element;
import ru.pavlik.chempred.server.model.converter.ElementConverter;
import ru.pavlik.chempred.server.utils.HibernateUtil;

import java.util.List;

public class ElementServiceImpl extends RemoteServiceServlet implements ElementService {

    private ElementConverter elementConverter = new ElementConverter();

    @Override
    public List<ElementDao> getElements() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Element");
        return query.list();
    }

    @Override
    public ElementDao getElement(String sign) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Element where symbol = :symbol");
        query.setParameter("symbol", sign);

        List<Element> elements = query.list();
        if (!elements.isEmpty()) {
            return elementConverter.convertToDao(elements.get(0));
        }
        return null;
    }
}
