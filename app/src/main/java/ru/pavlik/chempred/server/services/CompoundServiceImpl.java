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
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.client.services.compound.CompoundService;
import ru.pavlik.chempred.server.model.Compound;
import ru.pavlik.chempred.server.model.converter.AtomContainerConverter;
import ru.pavlik.chempred.server.model.converter.CompoundConverter;
import ru.pavlik.chempred.server.model.converter.ElementConverter;
import ru.pavlik.chempred.server.utils.BruttoUtils;
import ru.pavlik.chempred.server.utils.HibernateUtil;
import ru.pavlik.chempred.server.utils.SmilesUtils;

import java.util.ArrayList;
import java.util.List;

public class CompoundServiceImpl extends RemoteServiceServlet implements CompoundService {

    private ElementConverter elementConverter = new ElementConverter();
    private CompoundConverter compoundConverter = new CompoundConverter();
    private AtomContainerConverter atomContainerConverter = new AtomContainerConverter();

    @Override
    public StructureDao parseSmiles(String smiles) {
        return SmilesUtils.parseSmiles(smiles);
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
        session.getTransaction().commit();

        if (!compounds.isEmpty()) {
            Compound compound = (Compound) compounds.get(0);
            compoundDao.setId(compound.getId());
            compoundDao.setName(compound.getName());
        }

        IAtomContainer atomContainer = atomContainerConverter.convertToDB(structureDao);
        MolecularFormula molecularFormula = new MolecularFormula();
        for (IAtom atom : atomContainer.atoms()) {
            molecularFormula.addIsotope(atom);
            Integer hydrogenCount = atom.getImplicitHydrogenCount();
            if (hydrogenCount > 0) {
                molecularFormula.addIsotope(new Atom("H"), hydrogenCount);
            }
        }
        compoundDao.setBrutto(MolecularFormulaManipulator.getString(molecularFormula));

        return compoundDao;
    }

    @Override
    public List<CompoundDao> getCompounds() {
        List<CompoundDao> compoundDaoList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Compound");

        List<Compound> compounds = query.list();
        session.getTransaction().commit();
        for (Compound compound : compounds) {
            compoundDaoList.add(compoundConverter.convertToDao(compound));
        }

        return compoundDaoList;
    }

    @Override
    public List<CompoundDao> findCompounds(String searchQuery) {
        List<CompoundDao> compoundDaoList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Compound where name LIKE :query");
        query.setParameter("query", "%" + searchQuery + "%");

        List<Compound> compounds = query.list();
        session.getTransaction().commit();
        for (Compound compound : compounds) {
            compoundDaoList.add(compoundConverter.convertToDao(compound));
        }

        return compoundDaoList;
    }

    @Override
    public void addNewCompound(final CompoundDao newCompound) {
        newCompound.setBrutto(BruttoUtils.bruttoFromSmiles(newCompound.getSmiles()));

        final Compound compound = compoundConverter.convertToDB(newCompound);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.saveOrUpdate(compound);
        session.getTransaction().commit();
    }
}