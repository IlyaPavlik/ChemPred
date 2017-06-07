package ru.pavlik.chempred.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
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
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IAtom atom : atomContainer.atoms()) {
            descriptors.add(atom.getSymbol());
        }

        return descriptors;
    }

    public static List<String> getFragmentDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(compound.getSmiles());
        if (atomContainer == null) return descriptors;

        for (IAtom atom : atomContainer.atoms()) {
            descriptors.add(getAtomWithHydrogen(atom));
        }
        return descriptors;
    }

    public static List<String> getAtomAtomDescriptors(CompoundDao compound) {
        List<String> descriptors = new ArrayList<>();
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(compound.getSmiles());
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
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(compound.getSmiles());
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
