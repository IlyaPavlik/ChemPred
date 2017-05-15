package ru.pavlik.chempred.server.utils;

import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.DescriptorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class DescriptorUtils {

    private DescriptorUtils() {
    }

    public static List<String> getDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        for (DescriptorType type : DescriptorType.values()) {
            descriptors.addAll(getDescriptors(type, compound));
        }
        return descriptors;
    }

    public static List<String> getDescriptors(DescriptorType descriptorType, CompoundDao compound) {
        switch (descriptorType) {
            case ATOM:
                return getAtomDescriptors(compound);
            case FRAGMENT:
                return getFragmentDescriptors(compound);
            case ATOM_ATOM:
                return getAtomAtomDescriptors(compound);
            case FRAGMENT_FRAGMENT:
                return getFragmentFragmentDescriptors(compound);
        }
        return new ArrayList<>();
    }

    public static List<String> getAtomDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IAtom atom : atomContainer.atoms()) {
            descriptors.add(atom.getSymbol());
        }

        return descriptors;
    }

    public static List<String> getFragmentDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IAtom atom : atomContainer.atoms()) {
            descriptors.add(getAtomWithHydrogen(atom));
        }
        return descriptors;
    }

    public static List<String> getAtomAtomDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IBond bond : atomContainer.bonds()) {
            if (bond.getAtomCount() == 2) {
                List<IAtom> atoms = sortAtoms(bond.atoms());
                IAtom atom1 = atoms.get(0);
                IAtom atom2 = atoms.get(1);

                descriptors.add(atom1.getSymbol() + getStringLink(bond.getOrder()) + atom2.getSymbol());
            }
        }

        return descriptors;
    }

    public static List<String> getFragmentFragmentDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IBond bond : atomContainer.bonds()) {
            if (bond.getAtomCount() == 2) {
                List<IAtom> atoms = sortAtoms(bond.atoms());
                IAtom atom1 = atoms.get(0);
                IAtom atom2 = atoms.get(1);

                descriptors.add(getAtomWithHydrogen(atom1) + getStringLink(bond.getOrder()) + getAtomWithHydrogen(atom2));
            }
        }

        return descriptors;
    }

    @Nullable
    private static IAtomContainer getAtomContainer(String smiles) {
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer atomContainer;
        try {
            atomContainer = smilesParser.parseSmiles(smiles);
        } catch (InvalidSmilesException e) {
            log.error("Error occurred while parse smile: ", e);
            return null;
        }

        CDKAtomTypeMatcher atomTypeMatcher = CDKAtomTypeMatcher.getInstance(atomContainer.getBuilder());
        for (IAtom atom : atomContainer.atoms()) {
            try {
                IAtomType atomType = atomTypeMatcher.findMatchingAtomType(atomContainer, atom);
                AtomTypeManipulator.configure(atom, atomType);
            } catch (CDKException e) {
                log.error("Error occurred while configure atom container: ", e);
            }
        }

        try {
            CDKHydrogenAdder.getInstance(atomContainer.getBuilder()).addImplicitHydrogens(atomContainer);
        } catch (CDKException e) {
            log.error("Error occurred while adding hydrogens: ", e);
            return null;
        }

        return atomContainer;
    }

    private static String getAtomWithHydrogen(IAtom atom) {
        StringBuilder builder = new StringBuilder(atom.getSymbol());
        int hydrationCount = atom.getImplicitHydrogenCount();
        if (hydrationCount > 0) {
            builder.append("H");
            if (hydrationCount > 1) {
                builder.append(hydrationCount);
            }
        }
        return builder.toString();
    }

    private static String getStringLink(IBond.Order order) {
        switch (order) {
            case SINGLE:
                return "-";
            case DOUBLE:
                return "=";
            case TRIPLE:
                return "#";
            default:
                return "";
        }
    }

    private static List<IAtom> sortAtoms(Iterable<IAtom> atoms) {
        List<IAtom> atomList = new ArrayList<>();
        atoms.forEach(atomList::add);
        Collections.sort(atomList, (o1, o2) -> {
            int compare = Integer.compare(o1.getAtomicNumber(), o2.getAtomicNumber());

            if (compare == 0) {
                compare = Integer.compare(o1.getImplicitHydrogenCount(), o2.getImplicitHydrogenCount());
            }

            return compare;
        });
        return atomList;
    }

}
