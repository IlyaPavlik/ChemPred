package ru.pavlik.chempred.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.openscience.cdk.Atom;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.Element;
import ru.pavlik.chempred.server.model.converter.AtomContainerConverter;
import ru.pavlik.chempred.server.model.converter.ElementConverter;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.ArrayList;
import java.util.List;

public class CompoundServiceImpl extends RemoteServiceServlet implements CompoundService {

    private ElementConverter elementConverter = new ElementConverter();
    private AtomContainerConverter atomContainerConverter = new AtomContainerConverter();

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
        session.getTransaction().commit();
        return SmilesUtils.parseSmiles(elementDaoList, smiles);
    }

    @Override
    public String parseStructure(List<LinkDao> links) {
        return SmilesUtils.parseStructure(links);
    }

    @Override
    public CompoundDao getCompound(StructureDao structureDao) {
        CompoundDao compoundDao = new CompoundDao();
        String smiles = SmilesUtils.parseStructure(structureDao);
        compoundDao.setSmiles(smiles);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Compound where smiles = :smiles");
        query.setParameter("smiles", smiles);

        List compounds = query.list();

        if (!compounds.isEmpty()) {
            Compound compound = (Compound) compounds.get(0);
            compoundDao.setId(compound.getId());
            compoundDao.setName(compound.getName());
        }

        IAtomContainer atomContainer = atomContainerConverter.convertToDB(structureDao);
        MolecularFormula molecularFormula = new MolecularFormula();
        for (IAtom atom : atomContainer.atoms()) {
            molecularFormula.addIsotope(atom);
            molecularFormula.addIsotope(new Atom("H"), atom.getImplicitHydrogenCount());
        }
        compoundDao.setBrutto(MolecularFormulaManipulator.getString(molecularFormula));

        return compoundDao;
    }
}