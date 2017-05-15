package ru.pavlik.chempred.server.model.converter;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtomContainerConverter extends BaseConverter<StructureDao, IAtomContainer> {

    private LinkTypeConverter linkTypeConverter = new LinkTypeConverter();

    @Override
    public StructureDao convertToDao(IAtomContainer iAtomContainer) {
        return null;
    }

    @Override
    public IAtomContainer convertToDB(StructureDao structureDao) {
        IAtomContainer atomContainer = new AtomContainer();
        Map<ElementDao, IAtom> atoms = new HashMap<>();
        List<IBond> bonds = new ArrayList<>();

        for (ElementDao elementDao : structureDao.getElements()) {
            atoms.put(elementDao, new Atom(elementDao.getSymbol()));
        }

        for (LinkDao linkDao : structureDao.getLinks()) {
            IAtom source = atoms.get(linkDao.getElementSource());
            IAtom target = atoms.get(linkDao.getElementTarget());
            bonds.add(new Bond(source, target, linkTypeConverter.convertToDB(linkDao.getLinkType())));
        }

        for (IAtom atom : atoms.values()) {
            atomContainer.addAtom(atom);
        }

        for (IBond bond : bonds) {
            atomContainer.addBond(bond);
        }

        //configure valence of atoms
        CDKAtomTypeMatcher atomTypeMatcher = CDKAtomTypeMatcher.getInstance(atomContainer.getBuilder());
        for (IAtom atom : atomContainer.atoms()) {
            try {
                IAtomType atomType = atomTypeMatcher.findMatchingAtomType(atomContainer, atom);
                AtomTypeManipulator.configure(atom, atomType);
            } catch (CDKException e) {
                e.printStackTrace();
            }
        }

        //add hydrogen to atoms
        try {
            CDKHydrogenAdder.getInstance(atomContainer.getBuilder()).addImplicitHydrogens(atomContainer);
        } catch (CDKException e) {
            e.printStackTrace();
        }
        return atomContainer;
    }

}
