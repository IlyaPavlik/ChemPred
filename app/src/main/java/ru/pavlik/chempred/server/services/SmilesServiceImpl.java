package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.services.smiles.SmilesService;
import ru.pavlik.chempred.server.model.Element;
import ru.pavlik.chempred.server.model.converter.ElementConverter;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.ArrayList;
import java.util.List;

public class SmilesServiceImpl extends RemoteServiceServlet implements SmilesService {

    private ElementConverter elementConverter = new ElementConverter();

    @Override
    public StructureDao parseSmiles(String smiles) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Element");
        List<Element> elements = query.list();
        List<ElementDao> elementDaoList = new ArrayList<>();
        for (Element element : elements) {
            elementDaoList.add(elementConverter.convertToDao(element));
        }
        return SmilesUtils.parseSmiles(elementDaoList, smiles);
    }

    @Override
    public String parseStructure(List<LinkDao> links) {
        return SmilesUtils.parseStructure(links);
    }
}
