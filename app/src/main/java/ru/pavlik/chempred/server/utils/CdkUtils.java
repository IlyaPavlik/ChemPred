package ru.pavlik.chempred.server.utils;

import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

@Slf4j
public final class CdkUtils {

    private CdkUtils() {
    }

    @Nullable
    public static IAtomContainer getAtomContainer(String smiles) {
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
}
