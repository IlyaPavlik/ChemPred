package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.DescriptorType;
import ru.pavlik.chempred.client.services.descriptor.DescriptorService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.Descriptor;
import ru.pavlik.chempred.server.model.converter.CompoundConverter;
import ru.pavlik.chempred.server.utils.DescriptorUtils;
import ru.pavlik.chempred.server.utils.HibernateUtil;

import java.util.*;

public class DescriptorServiceImpl extends RemoteServiceServlet implements DescriptorService {

    private CompoundConverter compoundConverter = new CompoundConverter();

    @Override
    public List<String> getSourceDescriptors(DescriptorType descriptorType) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Descriptor where type = :type");
        query.setParameter("type", descriptorType);

        List<Descriptor> descriptors = query.list();
        session.getTransaction().commit();

        List<String> stringDescriptors = new ArrayList<>();
        for (Descriptor descriptor : descriptors) {
            stringDescriptors.add(descriptor.getName());
        }
        return stringDescriptors;
    }

    @Override
    public Map<DescriptorType, List<String>> getSourceDescriptors() {
        Map<DescriptorType, List<String>> sourceDescriptors = new TreeMap<>();

        for (DescriptorType type : DescriptorType.values()) {
            sourceDescriptors.put(type, getSourceDescriptors(type));
        }

        return sourceDescriptors;
    }

    @Override
    public Map<String, Integer> getCompoundDescriptors(CompoundDao compound) {
        Map<String, Integer> compoundDescriptors = new HashMap<>();

        List<String> descriptors = new ArrayList<>();
        descriptors.addAll(DescriptorUtils.getAtomDescriptors(compound));
        descriptors.addAll(DescriptorUtils.getFragmentDescriptors(compound));
        descriptors.addAll(DescriptorUtils.getAtomAtomDescriptors(compound));
        descriptors.addAll(DescriptorUtils.getFragmentFragmentDescriptors(compound));

        for (String descriptor : descriptors) {
            if (compoundDescriptors.containsKey(descriptor)) {
                compoundDescriptors.put(descriptor, compoundDescriptors.get(descriptor) + 1);
            } else {
                compoundDescriptors.put(descriptor, 1);
            }
        }

        return compoundDescriptors;
    }

    public void rebuildDescriptors() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query compoundsQuery = session.createQuery("from Compound");
        List<Compound> compounds = compoundsQuery.list();
        session.getTransaction().commit();


        session.beginTransaction();
        for (Compound compound : compounds) {
            CompoundDao compoundDao = compoundConverter.convertToDao(compound);

            for (DescriptorType type : DescriptorType.values()) {
                List<String> descriptors = DescriptorUtils.getDescriptors(type, compoundDao);
                for (String descriptorName : descriptors) {
                    try {
                        session.saveOrUpdate(new Descriptor(descriptorName, type));
                    } catch (NonUniqueObjectException e) {
                        //ignore, only unique value
                    }
                }
            }
            session.flush();
            session.clear();
        }
        session.getTransaction().commit();
    }
}
