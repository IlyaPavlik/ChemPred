package ru.pavlik.chempred.server.utils;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.server.model.converter.AtomContainerConverter;

import java.util.List;

public class SmilesUtils {

    private SmilesUtils() {
        throw new UnsupportedOperationException("Utils constructor unsupported");
    }

    public static StructureDao parseSmiles(String smiles) {
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(smiles);
        AtomContainerConverter atomContainerConverter = new AtomContainerConverter();
        return atomContainerConverter.convertToDao(atomContainer);
    }

    public static String parseStructure(StructureDao structureDao) {
        SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.Generic);
        AtomContainerConverter atomContainerConverter = new AtomContainerConverter();
        IAtomContainer atomContainer = atomContainerConverter.convertToDB(structureDao);
        try {
            return smilesGenerator.create(atomContainer);
        } catch (CDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static String parseStructure(List<LinkDao> links) {
        String resultSmiles = links.get(0).getElementSource().getSymbol();

        for (int i = 0; i < links.size(); i++) {
            boolean nextEl = containsNextElement(links, i);

            if (nextEl) resultSmiles += "(";
            switch (links.get(i).getLinkType()) {
                case DOUBLE:
                    resultSmiles += "=";
                    break;
                case TRIPLE:
                    resultSmiles += "#";
                    break;
            }

            resultSmiles += links.get(i).getElementTarget().getSymbol();

            if (nextEl) resultSmiles += ")";
        }

        return resultSmiles;
    }

    private static boolean containsNextElement(List<LinkDao> links, int startIndex) {
        if (startIndex + 1 > links.size()) return false;

        ElementDao sourceElement = links.get(startIndex).getElementSource();
        for (int i = startIndex + 1; i < links.size(); i++) {
            if (sourceElement == links.get(i).getElementSource()) {
                return true;
            }
        }

        return false;
    }
}
