package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.services.element.ElementService;
import ru.pavlik.chempred.server.model.Element;
import ru.pavlik.chempred.server.utils.HibernateUtil;

import java.util.List;

public class ElementServiceImpl extends RemoteServiceServlet implements ElementService {
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
            return convertToClient(elements.get(0));
        }
        return null;
    }

    private ElementDao convertToClient(Element element) {
        ElementDao elementDao = new ElementDao();
        elementDao.setId(element.getId());
        elementDao.setName(element.getName());
        elementDao.setGroup(element.getGroup());
        elementDao.setPeriod(element.getPeriod());
        elementDao.setSymbol(element.getSymbol());
        elementDao.setWeight(element.getWeight());
        elementDao.setElectronegativity(element.getElectronegativity());
        elementDao.setValence(element.getValence());
        return elementDao;
    }
}
